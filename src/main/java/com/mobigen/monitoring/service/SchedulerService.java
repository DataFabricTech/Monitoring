package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.mobigen.monitoring.model.enums.EventType.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    final SchedulerConfig schedulerConfig;

    private final ConcurrentLinkedDeque<GenericWrapper<Services>> servicesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ServicesHistory>> historiesQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ServicesConnect>> connectsQueue = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

    @PostConstruct
    public void init() {
        connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);
    }

    // 수집 cron과, 저장 cron 분리 필요
    @Scheduled(cron = "${scheduler.expression:0 5 * * * *}")
    public void collectData() {
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
                    .noneMatch(service -> UUID.fromString(service.get(ID.getName()).asText()).equals(existingService.getEntityID()));

            if (isDeleted && !existingService.isDeleted()) {
                var deletedServices = existingService.toBuilder()
                        .deleted(true)
                        .build();

                servicesQueue.add(new GenericWrapper<>(deletedServices, LocalDateTime.now()));
                historiesQueue.add(new GenericWrapper<>(ServicesHistory.builder()
                        .serviceID(deletedServices.getEntityID())
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

                // todo 1719989986575 이걸 가지고 LocalDateTime으로 바꾸는 로직 필요할듯?
                var service = Services.builder()
                        .entityID(serviceId)
                        .name(currentService.get(NAME.getName()).asText())
                        .createdAt(LocalDateTime.now()) // todo ???? 이게 now?
                        .serviceType(currentService.get(SERVICE_TYPE.getName()).asText())
                        .ownerName(currentService.get(UPDATED_BY.getName()).asText())
                        .connectionStatus(false)
                        .build();

                servicesQueue.add(new GenericWrapper<>(service, LocalDateTime.now()));
                historiesQueue.add(new GenericWrapper<>(ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(SERVICE_CREATE.getName())
                        .updateAt(LocalDateTime.now()) // todo 이거도 위아 같은 에러같은데?
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
            connectService.getDBItems(currentService, omDBItems);
        }
    }

    @Scheduled(cron = "${scheduler.expression:0 30 * * * *}")
    public void processData() {
        var now = LocalDateTime.now();
        var periodStart = now.minusMinutes(30); // todo 위의 간격

        List<Services> servicesList = processDeque(servicesQueue, periodStart, now);
        List<ServicesHistory> historiesList = processDeque(historiesQueue, periodStart, now);
        List<ServicesConnect> connectsList = processDeque(connectsQueue, periodStart, now);
        List<ModelRegistration> modelRegistrationList = processDeque(modelRegistrationQueue, periodStart, now);

        // todo 위의 List를 이용한 summary 및 저징 로직 필요
    }

    private <T> List<T> processDeque(ConcurrentLinkedDeque<GenericWrapper<T>> deque,
                                     LocalDateTime periodStart, LocalDateTime now) {
        return deque.stream()
                .filter(wrapper -> wrapper.getTimestamp().isAfter(periodStart) && wrapper.getTimestamp().isBefore(now))
                .map(GenericWrapper::getObject)
                .collect(Collectors.toList());
    }


    // todo userName을 이용한 매개변수가 필요할 듯하다?
    @Scheduled(cron = "${scheduler.expression:0 5 * * * *}")
    public void scheduler() {
        log.info("Monitoring Scheduler Start");
        List<ServicesHistory> histories = new ArrayList<>();

        // openMetadataServices
        var databaseServices = openMetadataService.getDatabaseServices();
        var storageServices = openMetadataService.getStorageServices();
        List<JsonNode> openMetadataServices = new ArrayList<>();
        databaseServices.forEach(openMetadataServices::add);
        storageServices.forEach(openMetadataServices::add);
        List<String> openMetadataServiceIds = new ArrayList<>();
        openMetadataServices.forEach(service -> {
            var id = service.get(ID.getName()).asText();
            openMetadataServiceIds.add(id);
        });

        // existServices
        var existServices = servicesService.getServicesList();
        List<String> existServicesIds = new ArrayList<>();
        existServices.forEach(service -> {
            if (!service.isDeleted()) {
                var id = service.getEntityID().toString();
                existServicesIds.add(id);
            }
        });

        // createdCheck
        for (var service : openMetadataServices) {
            if (!existServicesIds.contains(service.get(ID.getName()).asText())) {
                var serviceId = UUID.fromString(service.get(ID.getName()).asText());
                existServicesIds.add(service.get(ID.getName()).asText());
                servicesService.saveServices(service);
                existServices.add(Services.builder()
                        .entityID(serviceId)
                        .name(service.get(NAME.getName()).asText())
                        .createdAt(LocalDateTime.now())
                        .serviceType(service.get(SERVICE_TYPE.getName()).asText())
                        .ownerName(service.get(UPDATED_BY.getName()).asText())
                        .connectionStatus(false)
                        .build());
                histories.add(ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(SERVICE_CREATE.getName())
                        .updateAt(LocalDateTime.now())
                        .build());
            }
        }

        // deletedCheck
        for (var existServiceId : existServicesIds) {
            if (!openMetadataServiceIds.contains(existServiceId)) {
                var deletedServices = existServices.stream().filter(services ->
                        services.getEntityID().toString().equals(existServiceId)
                ).findFirst();

                deletedServices.ifPresent(service -> {
                    var newDeletedServices = deletedServices.get().toBuilder()
                            .deleted(true)
                            .build();

                    existServices.remove(deletedServices.get());
                    existServices.add(newDeletedServices);

                    histories.add(ServicesHistory.builder()
                            .serviceID(deletedServices.get().getEntityID())
                            .event(SERVICE_DELETED.getName())
                            .updateAt(LocalDateTime.now())
                            .build());

                });
            }
        }

        servicesService.saveServices(existServices);

        // connectionCheck & get Tables or Files
        for (var service : openMetadataServices) {
            var param = String.format("?q=%s&index=%s&from=0&size=0&deleted=false" +
                            "&query_filter={\"query\":{\"bool\":{}}}", service.get(NAME.getName()).asText(),
                    service.get(SERVICE_TYPE.getName()).asText().equalsIgnoreCase("s3") ||
                            service.get(SERVICE_TYPE.getName()).asText().equalsIgnoreCase("minio") ?
                            "container_search_index" : "table_search_index");
            var omDBItems = openMetadataService.getQuery(param).get("hits").get("total").get("value").asInt();
            connectService.getDBItems(service, omDBItems);
        }
        historyService.saveHistory(histories);
    }
}
