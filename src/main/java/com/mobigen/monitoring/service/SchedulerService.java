package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.DataCleanupConfig;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.*;
import com.mobigen.monitoring.utils.Utils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import static com.mobigen.monitoring.model.enums.Common.SCHEDULER;
import static com.mobigen.monitoring.model.enums.IngestionEventType.*;
import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.Metadata.RECENT_COLLECTED_TIME;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

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
    private TaskScheduler serviceCollectTaskScheduler;
    @Autowired
    private TaskScheduler ingestionCollectTaskScheduler;
    @Autowired
    private TaskScheduler saveTaskScheduler;
    @Autowired
    private TaskScheduler deleteTaskScheduler;
    private ScheduledFuture<?> serviceCollectScheduledFuture;
    private ScheduledFuture<?> ingestionCollectScheduledFuture;
    private ScheduledFuture<?> saveScheduledFuture;
    private ScheduledFuture<?> deleteScheduledFuture;

    private final OpenMetadataService openMetadataService;
    private final ServicesService servicesService;
    private final IngestionsService ingestionsService;
    private final ConnectionHistoryService connectionHistoryService;
    private final IngestionHistoryService ingestionHistoryService;
    private final ConnectionService connectionService;
    private final ModelRegistrationService modelRegistrationService;
    private final MetadataService metadataService;
    private final SchedulerConfig schedulerConfig;
    private final DataCleanupConfig dataCleanupConfig;


    private final ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<IngestionDTO>> ingestionsQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> connectionHistoriesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<IngestionHistoryDTO>> ingestionHistoriesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();
    private final HashMap<String, String> metadataQueue = new HashMap<>();

    private final Utils utils = new Utils();

    @PostConstruct
    public void init() {
        serviceCollectDataByScheduler(schedulerConfig.getCollectExpression());
        ingestionCollectDataByScheduler(schedulerConfig.getCollectExpression());
        saveData(schedulerConfig.getSaveExpression());
        deleteData(schedulerConfig.getDeleteExpression());
        connectionService.setDeque(servicesQueue, connectionHistoriesQueue, connectsQueue, modelRegistrationQueue);
    }

    public void setScheduler(SchedulerSettingDto schedulerSettingDto) {
        Optional.ofNullable(schedulerSettingDto.getCollectExpression())
                .ifPresent(this::updateCollectScheduler);

        Optional.ofNullable(schedulerSettingDto.getSaveExpression())
                .ifPresent(this::updateSaveScheduler);
    }


    private void serviceCollectDataByScheduler(String collectExpression) {
        if (serviceCollectScheduledFuture != null)
            serviceCollectScheduledFuture.cancel(false);

        serviceCollectScheduledFuture = serviceCollectTaskScheduler.schedule(() ->
                        serviceCollectData(SCHEDULER.getName())
                , new CronTrigger(collectExpression));
    }

    private void ingestionCollectDataByScheduler(String collectExpression) {
        if (ingestionCollectScheduledFuture != null)
            ingestionCollectScheduledFuture.cancel(false);

        ingestionCollectScheduledFuture = ingestionCollectTaskScheduler
                .schedule(this::ingestionCollectData, new CronTrigger(collectExpression));
    }

    private void saveData(String saveExpression) {
        if (saveScheduledFuture != null)
            saveScheduledFuture.cancel(false);

        saveScheduledFuture = saveTaskScheduler.schedule(this::saveData, new CronTrigger(saveExpression));
    }

    private void deleteData(String deleteExpression) {
        if (deleteScheduledFuture != null)
            deleteScheduledFuture.cancel(false);

        deleteScheduledFuture = deleteTaskScheduler.schedule(this::deleteData, new CronTrigger(deleteExpression));
    }


    private void updateCollectScheduler(String newCollectExpression) {
        serviceCollectDataByScheduler(newCollectExpression);
        ingestionCollectDataByScheduler(newCollectExpression);
    }

    private void updateSaveScheduler(String newSaveExpression) {
        saveData(newSaveExpression);
    }

    public void collectDataByUser(String userName) {
        serviceCollectData(userName);
        ingestionCollectData();
    }

    private void serviceCollectData(String userName) {
        log.info("Service Collect Data Start");
        JsonNode databaseServices = openMetadataService.getDatabaseServices();
        JsonNode storageServices = openMetadataService.getStorageServices();
        List<JsonNode> currentServices = new ArrayList<>();
        databaseServices.forEach(currentServices::add);
        storageServices.forEach(currentServices::add);

        var existingServices = servicesService.getServiceList();

        // Services deleted Check
        for (var existingService : existingServices) {
            var isDeleted = currentServices.stream()
                    .noneMatch(service -> UUID.fromString(service.get(ID.getName()).asText()).equals(existingService.getServiceID()));

            if (isDeleted && !existingService.isDeleted()) {
                var deletedServices = existingService.toBuilder()
                        .deleted(true)
                        .build();

                servicesQueue.add(new GenericWrapper<>(deletedServices,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }

        // Services Create Check
        for (var currentService : currentServices) {
            var existingServiceOpt = servicesService.getServices(UUID.fromString(currentService.get(ID.getName()).asText()));
            if (existingServiceOpt.isEmpty()) {
                var dateTime = currentService.get(UPDATED_AT.getName()).asLong();
                var service = ServiceDTO.builder()
                        .serviceID(UUID.fromString(currentService.get(ID.getName()).asText()))
                        .name(currentService.get(NAME.getName()).asText())
                        .displayName(utils.getAsTextOrNull(currentService.get(DISPLAY_NAME.getName())))
                        .createdAt(dateTime)
                        .updatedAt(dateTime)
                        .serviceType(currentService.get(SERVICE_TYPE.getName()).asText())
                        .ownerName(currentService.get(UPDATED_BY.getName()).asText())
                        .connectionStatus(DISCONNECTED)
                        .build();

                servicesQueue.add(new GenericWrapper<>(service,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }

        // Services Update Check
        for (var currentService : currentServices) {
            var existingServiceOpt = servicesService.getServices(UUID.fromString(currentService.get(ID.getName()).asText()));
            var currentUpdatedAt = currentService.get(UPDATED_AT.getName()).asLong();

            if (existingServiceOpt.isPresent() && currentUpdatedAt > existingServiceOpt.get().getUpdatedAt()) {
                var existingService = existingServiceOpt.get();

                servicesQueue.add(new GenericWrapper<>(existingService.toBuilder()
                        .displayName(utils.getAsTextOrNull(currentService.get(DISPLAY_NAME.getName())))
                        .updatedAt(currentUpdatedAt)
                        .build(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
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
            connectionService.getDBItems(currentService, omDBItems, userName);
        }

        metadataQueue.put(RECENT_COLLECTED_TIME.getName(),
                String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }

    private void ingestionCollectData() {
        log.info("Ingestion Collect Data Start");

        List<JsonNode> currentIngestions = new ArrayList<>();
        openMetadataService.getIngestions().forEach(currentIngestions::add);

        var existingIngestions = ingestionsService.getIngestionList();

        // Ingestion deleted Check
        for (var existingIngestion : existingIngestions) {
            var isDeleted = currentIngestions.stream()
                    .noneMatch(ingestion -> UUID.fromString(ingestion.get(ID.getName()).asText()).equals(existingIngestion.getIngestionID()));

            if (isDeleted && !existingIngestion.isDeleted()) {
                var deletedIngestions = existingIngestion.toBuilder()
                        .deleted(true)
                        .build();

                ingestionsQueue.add(new GenericWrapper<>(deletedIngestions,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

                var ingestionHistory = IngestionHistoryDTO.builder()
                        .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .ingestionID(deletedIngestions.getIngestionID())
                        .event(DELETED.getName())
                        .state("--")
                        .build();

                ingestionHistoriesQueue.add(new GenericWrapper<>(ingestionHistory,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }

        // Ingestion Update Check
        for (var currentIngestion : currentIngestions) {
            var existingIngestionOpt = ingestionsService.getIngestion(UUID.fromString(currentIngestion.get(ID.getName()).asText()));
            if (existingIngestionOpt.isPresent()) {
                if (!existingIngestionOpt.get().getDisplayName().equals(utils.getAsTextOrNull(currentIngestion.get(DISPLAY_NAME.getName())))) {
                    var dateTime = currentIngestion.get(UPDATED_AT.getName()).asLong();
                    var ingestionID = UUID.fromString(currentIngestion.get(ID.getName()).asText());
                    var ingestion = existingIngestionOpt.get().toBuilder()
                            .displayName(utils.getAsTextOrNull(currentIngestion.get(DISPLAY_NAME.getName())))
                            .updatedAt(dateTime)
                            .build();

                    ingestionsQueue.add(new GenericWrapper<>(ingestion,
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

                    var ingestionHistory = IngestionHistoryDTO.builder()
                            .eventAt(dateTime)
                            .ingestionID(ingestionID)
                            .event(UPDATED.getName())
                            .build();
                    ingestionHistoriesQueue.add(new GenericWrapper<>(ingestionHistory,
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                }
            }
        }

        // Ingestion Create check
        for (var currentIngestion : currentIngestions) {
            var existingIngestionOpt = ingestionsService.getIngestion(UUID.fromString(currentIngestion.get(ID.getName()).asText()));
            if (existingIngestionOpt.isEmpty()) {
                var dateTime = currentIngestion.get(UPDATED_AT.getName()).asLong();
                var ingestionID = UUID.fromString(currentIngestion.get(ID.getName()).asText());
                var ingestion = IngestionDTO.builder()
                        .ingestionID(ingestionID)
                        .name(currentIngestion.get(NAME.getName()).asText())
                        .displayName(utils.getAsTextOrNull(currentIngestion.get(DISPLAY_NAME.getName())))
                        .type(currentIngestion.get(PIPELINE_TYPE.getName()).asText())
                        .updatedAt(dateTime)
                        .serviceFQN(currentIngestion.get(SERVICE.getName()).get(FQN.getName()).asText())
                        .serviceID(UUID.fromString(currentIngestion.get(SERVICE.getName()).get(ID.getName()).asText()))
                        .build();

                ingestionsQueue.add(new GenericWrapper<>(ingestion,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

                var ingestionHistory = IngestionHistoryDTO.builder()
                        .eventAt(dateTime)
                        .ingestionID(ingestionID)
                        .event(CREATED.getName())
                        .state("--")
                        .build();

                ingestionHistoriesQueue.add(new GenericWrapper<>(ingestionHistory,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }

        // Ingestion updated Check
        for (var currentIngestion : currentIngestions) {
            var ingestionId = UUID.fromString(currentIngestion.get(ID.getName()).asText());
            var ingestionOpt = ingestionsService.getIngestion(ingestionId);
            var currentUpdatedAt = currentIngestion.get(UPDATED_AT.getName()).asLong();

            if (ingestionOpt.isPresent() && currentUpdatedAt > ingestionOpt.get().getUpdatedAt()) {
                var ingestion = ingestionOpt.get();

                ingestionsQueue.add(new GenericWrapper<>(ingestion.toBuilder()
                        .updatedAt(currentUpdatedAt)
                        .displayName(utils.getAsTextOrNull(currentIngestion.get(DISPLAY_NAME.getName())))
                        .build(),
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

                var ingestionHistory = IngestionHistoryDTO.builder()
                        .eventAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .ingestionID(ingestionId)
                        .event(UPDATED.getName())
                        .state("--")
                        .build();

                ingestionHistoriesQueue.add(new GenericWrapper<>(ingestionHistory,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }


        // Ingestion State history check
        for (var currentIngestion : currentIngestions) {
            List<JsonNode> currentIngestionHistories = new ArrayList<>();
            var ingestionHistories = openMetadataService.getIngestionState(
                    currentIngestion.get(SERVICE.getName()).get(FQN.getName()).asText(),
                    currentIngestion.get(NAME.getName()).asText());

            if (ingestionHistories != null)
                ingestionHistories.forEach(currentIngestionHistories::add);

            for (var currentIngestionHistory : currentIngestionHistories) {
                var ingestionHistory = ingestionHistoryService.getIngestionHistory(UUID.fromString(currentIngestionHistory.get(RUN_ID.getName()).asText()));
                if (ingestionHistory.isEmpty()) {
                    var runId = utils.getAsTextOrNull(currentIngestionHistory.get(RUN_ID.getName()));
                    var ingestionHistoryDTOBuilder = IngestionHistoryDTO.builder()
                            .eventAt(currentIngestionHistory.get(END_DATE.getName()).asLong())
                            .ingestionID(UUID.fromString(currentIngestion.get(ID.getName()).asText()))
                            .event(STATUS_CHANGE.getName())
                            .state(currentIngestionHistory.get(PIPELINE_STATE.getName()).asText());

                    if (runId != null)
                        ingestionHistoryDTOBuilder
                                .ingestionRunId(UUID.fromString(runId));

                    ingestionHistoriesQueue.add(new GenericWrapper<>(ingestionHistoryDTOBuilder.build(),
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                }
            }
        }
    }

    public void saveData() {
        log.info("Save Data Start");

        var now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        var cronExpression = schedulerConfig.getSaveExpression();

        var periodStart = LocalDateTime.now().minusMinutes(cronExpression.split(" ")[1].contains("/")
                ? Integer.parseInt(cronExpression.split(" ")[1].split("/")[1])
                : Integer.parseInt(cronExpression.split(" ")[1])).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        List<ServiceDTO> servicesList = processDeque(servicesQueue, periodStart, now);
        List<ConnectionHistoryDTO> connectionHistoriesList = processDeque(connectionHistoriesQueue, periodStart, now);
        List<ConnectionDTO> connectsList = processDeque(connectsQueue, periodStart, now);
        List<ModelRegistration> modelRegistrationList = processDeque(modelRegistrationQueue, periodStart, now);
        List<IngestionDTO> ingestionList = processDeque(ingestionsQueue, periodStart, now);
        List<IngestionHistoryDTO> ingestionHistoryList = processDeque(ingestionHistoriesQueue, periodStart, now);

        List<ServiceDTO> summarizedServicesList = summarizeServicesList(servicesList);
        List<ConnectionHistoryDTO> summarizedHistoryList = summarizeConnectionHistoriesList(connectionHistoriesList);
        List<ConnectionDTO> summarizedConnectList = summarizeConnectsList(connectsList);
        List<ModelRegistration> summarizedModelRegistrationList = summarizeModelRegistrationList(modelRegistrationList);

        servicesService.saveServices(summarizedServicesList);
        connectionHistoryService.saveConnectionHistory(summarizedHistoryList);
        connectionService.saveConnections(summarizedConnectList);
        modelRegistrationService.saveModelRegistrations(summarizedModelRegistrationList);
        ingestionsService.saveIngestions(ingestionList);
        ingestionHistoryService.saveIngestionHistories(ingestionHistoryList);
        metadataService.saveMetadata(metadataQueue);
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

    private List<ConnectionHistoryDTO> summarizeConnectionHistoriesList(List<ConnectionHistoryDTO> connectionHistories) {
        Map<UUID, List<ConnectionHistoryDTO>> groupedByServiceId = connectionHistories.stream()
                .collect(Collectors.groupingBy(ConnectionHistoryDTO::getServiceID));

        return groupedByServiceId.values().stream()
                .flatMap(group -> {
                    var firstUpdatedAt = group.stream().map(ConnectionHistoryDTO::getUpdatedAt).min(Long::compare).orElse(null);

                    return group.stream().map(connectionHistory -> group.getFirst().toBuilder()
                            .updatedAt(firstUpdatedAt).build());
                }).collect(Collectors.toList());

    }

    private List<ConnectionDTO> summarizeConnectsList(List<ConnectionDTO> servicesConnectsList) {
        Map<UUID, List<ConnectionDTO>> groupedByEntityID = servicesConnectsList.stream()
                .collect(Collectors.groupingBy(ConnectionDTO::getServiceID));

        return groupedByEntityID.values().stream()
                .map(SchedulerService::summarizeConnects)
                .collect(Collectors.toList());
    }

    private static ConnectionDTO summarizeConnects(List<ConnectionDTO> servicesConnectsList) {
        var firstConnects = servicesConnectsList.getFirst();

        var averageQueryExecutionTime = (long) servicesConnectsList.stream()
                .mapToLong(ConnectionDTO::getQueryExecutionTime)
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
                                     Long periodStart, Long now) {

        var result = deque.stream()
                .filter(wrapper -> periodStart < wrapper.getTimestamp() && wrapper.getTimestamp() < now)
                .map(GenericWrapper::getObject)
                .collect(Collectors.toList());

        deque.removeIf(wrapper -> wrapper.getTimestamp() < now);
        return result;
    }

    public void deleteData() {
        log.info("Delete Data Start");

        var retentionDays = dataCleanupConfig.getRetentionDays();
        var maximumRows = dataCleanupConfig.getMaximumRows();

        // 서비스 상태
        connectionHistoryService.deleteConnectionHistory(retentionDays);

        // 수집 히스토리
        ingestionHistoryService.deleteIngestionHistories(maximumRows);
    }
}
