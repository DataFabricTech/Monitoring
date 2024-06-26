package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.mobigen.monitoring.model.enums.EventType.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MonitoringService {
    final OpenMetadataService openMetadataService;
    final ServicesService servicesService;
    final HistoryService historyService;
    final ConnectService connectService;

    public MonitoringService(OpenMetadataService openMetadataService, ServicesService servicesService,
                             HistoryService historyService, ConnectService connectService) {
        this.openMetadataService = openMetadataService;
        this.servicesService = servicesService;
        this.historyService = historyService;
        this.connectService = connectService;
    }


    @Scheduled(cron = "${scheduler.expression:0 5 * * *}")
    public void scheduler() {
        log.debug("Monitoring Start");
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
                        .updatedAt(LocalDateTime.now())
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
                            .updatedAt(LocalDateTime.now())
                            .build());

                });
            }
        }

        // connectionCheck & get Tables of Files
        for (var service : openMetadataServices) {
            connectService.getDBItems(service);
        }


        servicesService.saveServices(existServices);
        historyService.saveHistory(histories);

    }
}
