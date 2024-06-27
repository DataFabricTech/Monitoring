package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/monitoring")
public class Monitoring {

    final ServicesService servicesService;
    final ConnectService connectService;
    final HistoryService historyService;
    final MonitoringService monitoringService;

    public Monitoring(ServicesService servicesService, ConnectService connectService, HistoryService historyService, MonitoringService monitoringService) {
        this.servicesService = servicesService;
        this.connectService = connectService;
        this.historyService = historyService;
        this.monitoringService = monitoringService;
    }

    @GetMapping("/statusCheck")
    public Integer statusCheck() {
        return 200;
    }

    // Services

    /**
     * Connect Status Summary
     *
     * @return ConnectStatusResponse
     */
    @GetMapping("/connectStatus")
    public recordModel.ConnectStatusResponse connectStatus() {
        return recordModel.ConnectStatusResponse.builder()
                .total(servicesService.getServicesCount())
                .connected(servicesService.countByConnectionStatusIsTrue())
                .disConnected(servicesService.countByConnectionStatusIsFalse())
                .build();
    }

    /**
     * @param serviceID Target Service Id
     * @param page      view's pages
     * @param size      view's size
     */
    @GetMapping("/connectStatus/{serviceID}")
    public Services connectStatus(@PathVariable("serviceID") String serviceID,
                                  @RequestParam(value = "page", required = false,
                                          defaultValue = "${entity.pageable_config.connect.page}") int page,
                                  @RequestParam(value = "size", required = false,
                                          defaultValue = "${entity.pageable_config.connect.size}") int size) {
        page--;
        var serviceId = UUID.fromString(serviceID);
        var service = servicesService.getServices(serviceId);
        var histories = historyService.getServiceConnectionHistories(serviceId, page, size);
        return service.toBuilder()
                .connects(null)
                .histories(histories)
                .build();
    }

    /**
     * Connect Response Time Average calculate using DBMS's function
     *
     * @return List<List < ServiceName ( String ), AverageTime ( Double )>>
     */
    @GetMapping("/responseTime")
    public List<recordModel.ConnectionAvgResponseTime> responseTimes(
            @RequestParam(value = "page", required = false,
                    defaultValue = "${entity.pageable_config.connect.page}") int page,
            @RequestParam(value = "size", required = false,
                    defaultValue = "${entity.pageable_config.connect.size}") int size) {
        page--;
        return connectService.getServiceConnectResponseTimeList(page, size);
    }

    @GetMapping("/responseTime/{serviceID}")
    public List<ServicesConnect> targetResponseTimes(@PathVariable("serviceID") String serviceID,
                                                     @RequestParam(value = "page", required = false,
                                                             defaultValue = "${entity.pageable_config.connect.page}") int page,
                                                     @RequestParam(value = "size", required = false,
                                                             defaultValue = "${entity.pageable_config.connect.size}") int size
    ) {
        page--;
        var serviceId = UUID.fromString(serviceID);
        return connectService.getServiceConnectResponseTime(serviceId, page, size);
    }

    /**
     * @param size
     * @return
     */
    @GetMapping("/eventHistory")
    public List<Services> eventHistory(@RequestParam(value = "size", required = false,
            defaultValue = "${entity.pageable_config.history.size}") int size) {
        var eventHistories = historyService.getServiceHistories(size);
        List<Services> servicesList = new ArrayList<>();
        for (var eventHistory : eventHistories) {
            var targetService = servicesService.getServices(eventHistory.getServiceID());
            List<ServicesHistory> events = new ArrayList<>();
            events.add(eventHistory);
            targetService = targetService.toBuilder()
                    .connects(null)
                    .histories(events)
                    .build();
            servicesList.add(targetService);
        }

        return servicesList;
    }

    @GetMapping("/eventHistory/{serviceID}")
    public Services eventHistory(@PathVariable("serviceID") String serviceID,
                                 @RequestParam(value = "page", required = false,
                                         defaultValue = "${entity.pageable_config.history.page}") int page,
                                 @RequestParam(value = "size", required = false,
                                         defaultValue = "${entity.pageable_config.history.size}") int size
    ) {
        page--;
        var eventHistories = historyService.getServiceHistories(UUID.fromString(serviceID), page, size);
        var targetService = servicesService.getServices(UUID.fromString(serviceID));
        targetService = targetService.toBuilder()
                .connects(null)
                .histories(eventHistories)
                .build();
        return targetService;
    }

    @GetMapping("/runSchedule")
    public void runSchedule() {
        monitoringService.scheduler();
    }
}


