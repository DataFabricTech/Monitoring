package com.mobigen.monitoring.controller;

import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.service.*;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;


@RestController
@RequestMapping("/v1/monitoring")
public class Monitoring {

    final ServicesService servicesService;
    final ConnectService connectService;
    final HistoryService historyService;

    public Monitoring(ServicesService servicesService, ConnectService connectService, HistoryService historyService) {
        this.servicesService = servicesService;
        this.connectService = connectService;
        this.historyService = historyService;
    }

    // Services

    /**
     * Connect Status
     *
     * @return Long[]{connected, disconnected}
     */
    @GetMapping("/connectStatusSummary")
    public Long[] connectStatusSummary() {
        var connected = servicesService.countByConnectionStatusIsTrue();
        var total = servicesService.getServicesCount();
        return new Long[]{connected, total - connected};
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

    /**
     * CreatedAt/UpdatedAt History
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     *
     * @return JsonArray(?)
     */
    @GetMapping("/upsertHistory")
    public List<Services> upsertHistory() {
        var upsertHistories = historyService.getUpsertHistory();
        List<Services> servicesList = new ArrayList<>();
        for (var upsertHistory : upsertHistories) {
            var targetServices = servicesService.getServices(upsertHistory.getServiceID());
            List<ServicesHistory> histories = new ArrayList<>();
            histories.add(upsertHistory);
            targetServices = targetServices.toBuilder()
                    .histories(histories)
                    .build();
            servicesList.add(targetServices);
        }
        return servicesList;
    }

    @GetMapping("/targetUpsertHistory/{serviceID}")
    public Services upsertHistory(@PathVariable String serviceID) {
        var upsertHistories = historyService.getUpsertHistory(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(upsertHistories.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .histories(upsertHistories)
                .build();
        return targetService;
    }

    /**
     * Connect Response Time Average calculate using DBMS's function
     *
     * @return List<List < ServiceName ( String ), AverageTime ( Double )>>
     */
    @GetMapping("/responseTime")
    public List<Object[]> responseTimes() {
        return connectService.getServiceConnectList();
    }

    @GetMapping("/responseTimes/{serviceID}")
    public Services targetResponseTimes(@PathVariable String serviceID) {
        var responseTime = connectService.getServiceConnectList(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(responseTime.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .connects(responseTime)
                .build();

        return targetService;
    }

    /**
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return
     */
    @GetMapping("/eventHistory")
    public List<Services> eventHistory() {
        var eventHistories = historyService.getServiceHistory();
        List<Services> servicesList = new ArrayList<>();
        for (var eventHistory : eventHistories) {
            var targetService = servicesService.getServices(eventHistory.getServiceID());
            List<ServicesHistory> events = new ArrayList<>();
            events.add(eventHistory);
            targetService = targetService.toBuilder()
                    .histories(events)
                    .build();
            servicesList.add(targetService);
        }

        return servicesList;
    }

    @GetMapping("/eventHistory/{serviceID}")
    public Services eventHistory(@PathVariable String serviceID) {
        var eventHistories = historyService.getServiceHistory(UUID.fromString(serviceID));
        var targetService = servicesService.getServices(eventHistories.get(0).getServiceID());
        targetService = targetService.toBuilder()
                .histories(eventHistories)
                .build();
        return targetService;
    }

    @PostMapping("/databaseService")
    public void databaseService(@RequestBody String requestData) {
        /**
         * 구별하는 것 만들기
         *
         * 1. Test Connection에 대한 결과 값을 통한 Connected/Disconnected 저장
         *  1.1. ServiceConnect
         *  1.2. Test Connection을 통하여 걸린 시간 저장
         * 2. StorageServices/DatabasesService 생성 및 수정
         *  2.1. Recent Services or upsertHistory
         * 3. Event History 저장
         *  3.1. DatabasesServices, Databases, Schemas, Tables
         *  3.2. StorageServices, Containers, Children
         * 4. alert 생성 방법
         *        "resources":[
         *          "all"
         *       ]
         *       이것을 보면 원하는 target entity만 적으면 될 것 처럼 보인다.
         */
        var jsonObj = parseJson(requestData);
        switch (jsonObj.get("eventType").toString().toLowerCase()) {
            case "entitycreated":
                System.out.println("service - entity Create function");
            case "entitydeleted":
                System.out.println("service - entity Deleted function");

        }
    }

    @PostMapping("/receiver")
    public void receive(@RequestBody String requestData) {
        var requestJsonObj = parseJson(requestData);
        var entityJsonObj = parseJson(requestJsonObj.get(ENTITY).toString());
        var changeJsonObj = parseJson(requestJsonObj.get(CHANGE_DESCRIPTION).toString());

        var eventType = requestJsonObj.get(EVENT_TYPE).toString();
        var serviceId = UUID.fromString(entityJsonObj.get("id").toString());
        // TestConnection
        if (requestJsonObj.get(ENTITY_TYPE).toString().equalsIgnoreCase(WORKFLOW.getName())) {
            var entityId = UUID.fromString(requestJsonObj.get(ENTITY_ID).toString());
            var serviceConnect = connectService.getServiceConnect(entityId);
            var instant = Instant.ofEpochMilli((Long) requestJsonObj.get("timestamp"));
            var timestamp = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            if (serviceConnect == null) {
                serviceConnect = ServicesConnect.builder()
                        .entityID(entityId)
                        .serviceID(serviceId)
                        .startTimestamp(timestamp)
                        .build();
                connectService.saveConnect(serviceConnect);
            }
            if (serviceConnect != null && eventType.equalsIgnoreCase(ENTITY_DELETED.getName())){
                serviceConnect = serviceConnect.toBuilder()
                        .endTimestamp(serviceConnect.getEndTimestamp() == null ?
                                timestamp :
                                serviceConnect.getEndTimestamp().isBefore(timestamp) ?
                                        timestamp : serviceConnect.getEndTimestamp())
                        .build();
                connectService.saveConnect(serviceConnect);
            }
        } else {
            var services = servicesService.getServices(serviceId);
            if (services == null) {
                var history = ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(entityJsonObj.get(ENTITY_TYPE).toString())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .description(changeJsonObj.toJSONString())
                        .build();
                List<ServicesHistory> histories = new ArrayList<>();
                histories.add(history);
                var service = Services.builder()
                        .serviceID(serviceId)
                        .name(entityJsonObj.get("name").toString())
                        .databaseType(entityJsonObj.get(SERVICE_TYPE).toString())
                        .ownerName(requestJsonObj.get(USER_NAME).toString())
                        .connectionStatus(false)
                        .histories(histories)
                        .build();
                servicesService.saveServices(service);
            } else {
                var history = historyService.getServiceHistory(serviceId).get(0);
                history = history.toBuilder()
                        .event(eventType)
                        .updatedAt(LocalDateTime.now())
                        .description(changeJsonObj.toJSONString())
                        .build();
                historyService.saveServiceCreate(history);
            }
        }

    }

    @PostMapping("/databaseSchema")
    public void databaseSchema(@RequestBody String requestData) {
        var jsonObj = parseJson(requestData);
    }

    @PostMapping("/table")
    public void table(@RequestBody String requestData) {
        var jsonObj = parseJson(requestData);
    }

    @PostMapping("/storageService")
    public void storageService(@RequestBody String requestData) {
        var jsonObj = parseJson(requestData);
    }

    public JSONObject parseJson(String requestBody) {
        try {
            var jsonParser = new JSONParser(requestBody);
            return (JSONObject) jsonParser.parse();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
