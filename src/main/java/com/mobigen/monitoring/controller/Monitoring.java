package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.dto.Services;
import com.mobigen.monitoring.dto.ServicesChange;
import com.mobigen.monitoring.dto.ServicesEvent;
import com.mobigen.monitoring.service.ChangeService;
import com.mobigen.monitoring.service.ConnectService;
import com.mobigen.monitoring.service.EventService;
import com.mobigen.monitoring.service.ServicesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class Monitoring {

    final ServicesService servicesService;
    final ConnectService connectService;
    final ChangeService changeService;
    final EventService eventService;

    public Monitoring(ServicesService servicesService, ConnectService connectService, ChangeService changeService, EventService eventService) {
        this.servicesService = servicesService;
        this.connectService = connectService;
        this.changeService = changeService;
        this.eventService = eventService;
    }

    // Services

    /**
     * Connect Status
     * @return connected&total
     */
    @GetMapping("/connectStatusSummary")
    public Object connectStatusSummary() {
        var connected = servicesService.countByConnectionStatusIsTrue();
        var total = servicesService.getServicesCount();
        return null;
    }

    /**
     * get Target Services with Connect,
     *
     * @param serviceID service's ID
     * @return target Services
     */
    @GetMapping("/targetServices/{serviceID}")
    public Services targetServices(@PathVariable String serviceID) {
        return servicesService.getServices(UUID.fromString(serviceID));
    }

    // ChangeService

    /**
     * CreatedAt/UpdatedAt History
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * @return JsonArray(?)
     */
    @GetMapping("/upsertHistory")
    public List<Services> upsertHistory() {
        var upsertHistories = changeService.getServiceRecentChange();
        List<Services> servicesList = new ArrayList<>();
        for (ServicesChange upsertHistory: upsertHistories) {
            var targetServices = servicesService.getServices(upsertHistory.getServiceID());
            List<ServicesChange> changes = new ArrayList<>();
            changes.add(upsertHistory);
            targetServices.toBuilder()
                    .changes(changes)
                    .build();
            servicesList.add(targetServices);
        }
        return servicesList;
    }

    @GetMapping("/targetUpsertHistory/{serviceID}")
    public Services upsertHistory(@PathVariable String serviceID) {
        var upsertHistories = changeService.getServiceRecentChange(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(upsertHistories.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .changes(upsertHistories)
                .build();
        return targetService;
    }

    // ConnectService

    /**
     * Connect Response Time Average calculate using DBMS's function
     * @return List<List<ServiceName(String), AverageTime(Double)>>
     */
    @GetMapping("/responseTime")
    public List<Object[]> responseTimes() {
        return connectService.getServiceConnect();
    }

    @GetMapping("/responseTimes/{serviceID}")
    public Services targetResponseTimes(@PathVariable String serviceID) {
        var responseTime = connectService.getServiceConnect(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(responseTime.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .connects(responseTime)
                .build();

        return targetService;
    }

    // EventService

    /**
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return
     */
    @GetMapping("/eventHistory")
    public Object eventHistory() {
        var eventHistories = eventService.getServiceEvent();
        List<Services> servicesList = new ArrayList<>();
        for (var eventHistory: eventHistories) {
            var targetService = servicesService.getServices(eventHistory.getServiceID());
            List<ServicesEvent> events = new ArrayList<>();
            events.add(eventHistory);
            targetService = targetService.toBuilder()
                    .events(events)
                    .build();
            servicesList.add(targetService);
        }

        return servicesList;
    }

    @GetMapping(" /eventHistory/{serviceID}")
    public Object eventHistory(@PathVariable String serviceID) {
        var eventHistories = eventService.getServiceEvent(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(eventHistories.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .events(eventHistories)
                .build();
        return targetService;
    }
}
