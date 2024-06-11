package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.dto.Services;
import com.mobigen.monitoring.service.ChangeService;
import com.mobigen.monitoring.service.ConnectService;
import com.mobigen.monitoring.service.EventService;
import com.mobigen.monitoring.service.ServicesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    public Object upsertHistory() {
        var upsertHistories = changeService.getServiceRecentChange();
        for (var upsertHistory: upsertHistories) {

        }
        return null;
    }


    @GetMapping("/targetUpsertHistory/{serviceID}")
    public Object upsertHistory(@PathVariable String serviceID) {
        var upsertHistories = changeService.getServiceRecentChange(UUID.fromString(serviceID));
        return null;
    }

    // ConnectService

    /**
     * Connect Response Time Average calculate using DBMS's function
     * @return List<List<ServiceName(String), AverageTime(Double)>>
     */
    @GetMapping("/responseTime")
    public Long responseTime() {
        var avgResponseTime = connectService.getServiceConnect();
        return null;
    }

    @GetMapping("/responseTimes/{serviceID}")
    public Object targetConnectStatus(@PathVariable String serviceID) throws Exception {
        var responseTimes = connectService.getServiceConnect(UUID.fromString(serviceID));
        return null;
    }

    // EventService
    @GetMapping("/eventHistory")
    public Object eventHistory() {
        var eventHistories = eventService.getServiceEvent();
        return null;
    }

    @GetMapping(" /eventHistory/{serviceID}")
    public Object eventHistory(@PathVariable String serviceID) {
        var eventHistories = eventService.getServiceEvent(UUID.fromString(serviceID));
        return null;
    }
}
