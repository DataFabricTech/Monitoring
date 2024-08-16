package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.*;
import com.mobigen.monitoring.model.dto.compositeKeys.SummarizeHistoryKey;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import static com.mobigen.monitoring.model.enums.Common.SCHEDULER;
import static com.mobigen.monitoring.model.enums.EventType.*;
import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    @Autowired
    private TaskScheduler collectTaskScheduler;
    @Autowired
    private TaskScheduler saveTaskScheduler;
    private ScheduledFuture<?> collectScheduledFuture;
    private ScheduledFuture<?> saveScheduledFuture;

    private final OpenMetadataService openMetadataService;
    private final ServicesService servicesService;
    private final HistoryService historyService;
    private final ConnectService connectService;
    private final ModelRegistrationService modelRegistrationService;
    private final SchedulerConfig schedulerConfig;

    private final ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

    @PostConstruct
    public void init() {
        collectDataByScheduler(schedulerConfig.getCollectExpression());
        saveData(schedulerConfig.getSaveExpression());
        connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);
    }

    public void setScheduler(SchedulerSettingDto schedulerSettingDto) {
        Optional.ofNullable(schedulerSettingDto.getCollectExpression())
                .ifPresent(this::updateCollectScheduler);

        Optional.ofNullable(schedulerSettingDto.getSaveExpression())
                .ifPresent(this::updateSaveScheduler);
    }


    private void collectDataByScheduler(String collectExpression) {
        if (collectScheduledFuture != null)
            collectScheduledFuture.cancel(false);

        collectScheduledFuture = collectTaskScheduler.schedule(() -> {
            collectData(SCHEDULER.getName());
        }, new CronTrigger(collectExpression));
    }

    private void saveData(String saveExpression) {
        if (saveScheduledFuture != null)
            saveScheduledFuture.cancel(false);

        saveScheduledFuture = saveTaskScheduler.schedule(this::saveData, new CronTrigger(saveExpression));
    }


    private void updateCollectScheduler(String newCollectExpression) {
        collectDataByScheduler(newCollectExpression);
    }

    private void updateSaveScheduler(String newSaveExpression) {
        saveData(newSaveExpression);
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
                historiesQueue.add(new GenericWrapper<>(HistoryDTO.builder()
                        .serviceID(deletedServices.getServiceID())
                        .event(SERVICE_DELETED)
                        .description(SERVICE_DELETED.getName())
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
                var service = ServiceDTO.builder()
                        .serviceID(serviceId)
                        .name(currentService.get(NAME.getName()).asText())
                        .createdAt(dateTime)
                        .serviceType(currentService.get(SERVICE_TYPE.getName()).asText())
                        .ownerName(currentService.get(UPDATED_BY.getName()).asText())
                        .connectionStatus(DISCONNECTED)
                        .build();

                servicesQueue.add(new GenericWrapper<>(service, LocalDateTime.now()));
                historiesQueue.add(new GenericWrapper<>(HistoryDTO.builder()
                        .serviceID(serviceId)
                        .event(SERVICE_CREATE)
                        .description(SERVICE_CREATE.name())
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

    public void saveData() {
        log.info("Save Data Start");
        var now = LocalDateTime.now();
        var cronExpression = schedulerConfig.getSaveExpression();
        var periodStart = now.minusMinutes(cronExpression.split(" ")[1].contains("/")
                ? Integer.parseInt(cronExpression.split(" ")[1].split("/")[1])
                : Integer.parseInt(cronExpression.split(" ")[1]));

        List<ServiceDTO> servicesList = processDeque(servicesQueue, periodStart, now);
        List<HistoryDTO> historiesList = processDeque(historiesQueue, periodStart, now);
        List<ConnectDTO> connectsList = processDeque(connectsQueue, periodStart, now);
        List<ModelRegistration> modelRegistrationList = processDeque(modelRegistrationQueue, periodStart, now);

        List<ServiceDTO> summarizedServicesList = summarizeServicesList(servicesList);
        List<HistoryDTO> summarizedHistoryList = summarizeHistoriesList(historiesList);
        List<ConnectDTO> summarizedConnectList = summarizeConnectsList(connectsList);
        List<ModelRegistration> summarizedModelRegistrationList = summarizeModelRegistrationList(modelRegistrationList);

        servicesService.saveServices(summarizedServicesList);
        historyService.saveHistory(summarizedHistoryList);
        connectService.saveConnects(summarizedConnectList);
        modelRegistrationService.saveModelRegistrations(summarizedModelRegistrationList);
    }

    private List<ServiceDTO> summarizeServicesList(List<ServiceDTO> servicesList) {
        Map<UUID, List<ServiceDTO>> groupedByEntityID = servicesList.stream()
                .collect(Collectors.groupingBy(ServiceDTO::getServiceID));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeServices)
                .collect(Collectors.toList());
    }

    private static ServiceDTO summarizeServices(List<ServiceDTO> servicesList) {
        var firstServices = servicesList.getFirst();

        var deleted = servicesList.stream().anyMatch(ServiceDTO::isDeleted);
        var lastConnectionStatus = servicesList.getLast().getConnectionStatus();
        var earliestCreatedAt = servicesList.stream()
                .min(Comparator.comparing(ServiceDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .get()
                .getCreatedAt();

        return firstServices.toBuilder()
                .deleted(deleted)
                .connectionStatus(lastConnectionStatus)
                .createdAt(earliestCreatedAt)
                .build();
    }

    private List<HistoryDTO> summarizeHistoriesList(List<HistoryDTO> servicesHistories) {
        List<String> targetEvents = List.of(CONNECTION_CHECK.getName(), DISCONNECTED.getName(),
                CONNECTED.getName(), CONNECT_ERROR.getName());

        Map<SummarizeHistoryKey, List<HistoryDTO>> groupedByServiceIDAndEvent = servicesHistories.stream()
                .collect(Collectors.groupingBy(history -> new SummarizeHistoryKey(history.getServiceID(), history.getEvent())));

        return groupedByServiceIDAndEvent.values().stream()
                .flatMap(group -> {
                    if (targetEvents.contains(group.get(0).getEvent())) {
                        var firstUpdatedAt = group.stream()
                                .map(HistoryDTO::getUpdateAt)
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

    private List<ConnectDTO> summarizeConnectsList(List<ConnectDTO> servicesConnectsList) {
        Map<UUID, List<ConnectDTO>> groupedByEntityID = servicesConnectsList.stream()
                .collect(Collectors.groupingBy(ConnectDTO::getServiceID));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeConnects)
                .collect(Collectors.toList());
    }

    private static ConnectDTO summarizeConnects(List<ConnectDTO> servicesConnectsList) {
        var firstConnects = servicesConnectsList.getFirst();

        var averageQueryExecutionTime = (long) servicesConnectsList.stream()
                .mapToLong(ConnectDTO::getQueryExecutionTime)
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
