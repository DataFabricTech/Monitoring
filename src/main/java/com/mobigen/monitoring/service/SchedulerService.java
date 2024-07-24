package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.*;
import com.mobigen.monitoring.model.dto.compositeKeys.SummarizeHistoryKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.mobigen.monitoring.model.enums.Common.SCHEDULER;
import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.EventType.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    final OpenMetadataService openMetadataService;
    final ServicesService servicesService;
    final HistoryService historyService;
    final ConnectService connectService;
    final ModelRegistrationService modelRegistrationService;
    final SchedulerConfig schedulerConfig;

    private final ConcurrentLinkedDeque<GenericWrapper<Services>> servicesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ServicesHistory>> historiesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ServicesConnect>> connectsQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

    @PostConstruct
    public void init() {
        connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);
    }

    public void setScheduler(SchedulerSettingDto schedulerSettingDto) {
        schedulerConfig.setCollectExpression(Optional.ofNullable(schedulerSettingDto.getCollectExpression())
                        .orElse(schedulerConfig.getCollectExpression()));

        schedulerConfig.setSaveExpression(Optional.ofNullable(schedulerSettingDto.getSaveExpression())
                .orElse(schedulerConfig.getSaveExpression()));
    }

    public void getScheduler() {
        System.out.println(schedulerConfig.getCollectExpression());
        System.out.println(schedulerConfig.getSaveExpression());
    }

    @Scheduled(cron = "${scheduler.collectExpression:0 0/5 * * * *}")
    public void collectDataByScheduler() {
        collectData(SCHEDULER.getName());
    }

    public void collectDataByUser(String userName) {
        collectData(userName);
    }

    private void collectData(String userName) {
        log.info("Collect Data Start");
        JsonNode databaseServices = openMetadataService.getDatabaseServices();
        JsonNode storageServices = openMetadataService.getStorageServices();
        List<JsonNode> currentServices = new ArrayList<>();
        databaseServices.forEach(currentServices::add);
        storageServices.forEach(currentServices::add);

        var existingServices = servicesService.getServicesList();

        // deleted Check
        for (var existingService : existingServices) {
            var isDeleted = currentServices.stream()
                    .noneMatch(service -> UUID.fromString(service.get(ID.getName()).asText()).equals(existingService.getServiceID()));

            if (isDeleted && !existingService.isDeleted()) {
                var deletedServices = existingService.toBuilder()
                        .deleted(true)
                        .build();

                servicesQueue.add(new GenericWrapper<>(deletedServices, LocalDateTime.now()));
                historiesQueue.add(new GenericWrapper<>(ServicesHistory.builder()
                        .serviceID(deletedServices.getServiceID())
                        .event(SERVICE_DELETED.getName())
                        .updateAt(LocalDateTime.now())
                        .build(), LocalDateTime.now()));
            }
        }

        // createdCheck
        for (var currentService : currentServices) {
            var existingServiceOpt = servicesService.getServices(UUID.fromString(currentService.get(ID.getName()).asText()));
            if (existingServiceOpt.isEmpty()) {
                var serviceId = UUID.fromString(currentService.get(ID.getName()).asText());

                var dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(
                        currentService.get(UPDATED_AT.getName()).asLong()), ZoneId.systemDefault());
                var service = Services.builder()
                        .serviceID(serviceId)
                        .name(currentService.get(NAME.getName()).asText())
                        .createdAt(dateTime)
                        .serviceType(currentService.get(SERVICE_TYPE.getName()).asText())
                        .ownerName(currentService.get(UPDATED_BY.getName()).asText())
                        .connectionStatus(DISCONNECTED)
                        .build();

                servicesQueue.add(new GenericWrapper<>(service, LocalDateTime.now()));
                historiesQueue.add(new GenericWrapper<>(ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(SERVICE_CREATE.getName())
                        .updateAt(dateTime)
                        .build(), LocalDateTime.now()));
            }
        }

        // connectionCheck & get Tables or Files
        for (var currentService : currentServices) {
            var param = String.format("?q=%s&index=%s&from=0&size=0&deleted=false" +
                            "&query_filter={\"query\":{\"bool\":{}}}", currentService.get(NAME.getName()).asText(),
                    currentService.get(SERVICE_TYPE.getName()).asText().equalsIgnoreCase("s3") ||
                            currentService.get(SERVICE_TYPE.getName()).asText().equalsIgnoreCase("minio") ?
                            "container_search_index" : "table_search_index");
            var omDBItems = openMetadataService.getQuery(param).get("hits").get("total").get("value").asInt();
            connectService.getDBItems(currentService, omDBItems, userName);
        }
    }

    @Scheduled(cron = "${scheduler.saveExpression:0 0/30 * * * *}")
    public void saveData() {
        log.info("Save Data Start");
        var now = LocalDateTime.now();
        var cronExpression = schedulerConfig.getSaveExpression();
        var periodStart = now.minusMinutes(cronExpression.split(" ")[1].contains("/")
                ? Integer.parseInt(cronExpression.split(" ")[1].split("/")[1])
                : Integer.parseInt(cronExpression.split(" ")[1]));

        List<Services> servicesList = processDeque(servicesQueue, periodStart, now);
        List<ServicesHistory> historiesList = processDeque(historiesQueue, periodStart, now);
        List<ServicesConnect> connectsList = processDeque(connectsQueue, periodStart, now);
        List<ModelRegistration> modelRegistrationList = processDeque(modelRegistrationQueue, periodStart, now);

        List<Services> summarizedServicesList = summarizeServicesList(servicesList);
        List<ServicesHistory> summarizedHistoryList = summarizeHistoriesList(historiesList);
        List<ServicesConnect> summarizedConnectList = summarizeConnectsList(connectsList);
        List<ModelRegistration> summarizedModelRegistrationList = summarizeModelRegistrationList(modelRegistrationList);

        servicesService.saveServices(summarizedServicesList);
        historyService.saveHistory(summarizedHistoryList);
        connectService.saveConnects(summarizedConnectList);
        modelRegistrationService.saveModelRegistrations(summarizedModelRegistrationList);
    }

    private List<Services> summarizeServicesList(List<Services> servicesList) {
        Map<UUID, List<Services>> groupedByEntityID = servicesList.stream()
                .collect(Collectors.groupingBy(Services::getServiceID));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeServices)
                .collect(Collectors.toList());
    }

    private static Services summarizeServices(List<Services> servicesList) {
        var firstServices = servicesList.getFirst();

        var deleted = servicesList.stream().anyMatch(Services::isDeleted);
        var lastConnectionStatus = servicesList.getLast().getConnectionStatus();
        var earliestCreatedAt = servicesList.stream()
                .min(Comparator.comparing(Services::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .get()
                .getCreatedAt();

        return firstServices.toBuilder()
                .deleted(deleted)
                .connectionStatus(lastConnectionStatus)
                .createdAt(earliestCreatedAt)
                .build();
    }

    private List<ServicesHistory> summarizeHistoriesList(List<ServicesHistory> servicesHistories) {
        List<String> targetEvents = List.of(CONNECTION_CHECK.getName(), DISCONNECTED.getName(),
                CONNECTED.getName(), CONNECT_ERROR.getName());

        Map<SummarizeHistoryKey, List<ServicesHistory>> groupedByServiceIDAndEvent = servicesHistories.stream()
                .collect(Collectors.groupingBy(history -> new SummarizeHistoryKey(history.getServiceID(), history.getEvent())));

        return groupedByServiceIDAndEvent.values().stream()
                .flatMap(group -> {
                    if (targetEvents.contains(group.get(0).getEvent())) {
                        var firstUpdatedAt = group.stream()
                                .map(ServicesHistory::getUpdateAt)
                                .min(LocalDateTime::compareTo)
                                .orElse(null);

                        return group.stream().map(ServicesHistory -> group.get(0).toBuilder()
                                .updateAt(firstUpdatedAt)
                                .build());
                    } else {
                        return group.stream();
                    }
                }).collect(Collectors.toList());
    }

    private List<ServicesConnect> summarizeConnectsList(List<ServicesConnect> servicesConnectsList) {
        Map<UUID, List<ServicesConnect>> groupedByEntityID = servicesConnectsList.stream()
                .collect(Collectors.groupingBy(ServicesConnect::getServiceID));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeConnects)
                .collect(Collectors.toList());
    }

    private static ServicesConnect summarizeConnects(List<ServicesConnect> servicesConnectsList) {
        var firstConnects = servicesConnectsList.getFirst();

        var averageQueryExecutionTime = (long) servicesConnectsList.stream()
                .mapToLong(ServicesConnect::getQueryExecutionTime)
                .average()
                .orElse(0.0);

        return firstConnects.toBuilder()
                .queryExecutionTime(averageQueryExecutionTime)
                .build();
    }

    private List<ModelRegistration> summarizeModelRegistrationList(List<ModelRegistration> modelRegistrationList) {
        Map<UUID, List<ModelRegistration>> groupedByEntityID = modelRegistrationList.stream()
                .collect(Collectors.groupingBy(ModelRegistration::getServiceId));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeModelRegistration)
                .collect(Collectors.toList());
    }

    private static ModelRegistration summarizeModelRegistration(List<ModelRegistration> modelRegistrationList) {
        return modelRegistrationList.getLast();
    }


    private <T> List<T> processDeque(ConcurrentLinkedDeque<GenericWrapper<T>> deque,
                                     LocalDateTime periodStart, LocalDateTime now) {
        return deque.stream()
                .filter(wrapper -> wrapper.getTimestamp().isAfter(periodStart) && wrapper.getTimestamp().isBefore(now))
                .map(GenericWrapper::getObject)
                .collect(Collectors.toList());
    }
}
