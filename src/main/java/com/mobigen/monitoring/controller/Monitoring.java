package com.mobigen.monitoring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesChild;
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
    final ServicesChildService servicesChildService;
    final OpenMetadataConfig openMetadataConfig;

    public Monitoring(ServicesService servicesService, ConnectService connectService, HistoryService historyService, ServicesChildService servicesChildService, OpenMetadataConfig openMetadataConfig) {
        this.servicesService = servicesService;
        this.connectService = connectService;
        this.historyService = historyService;
        this.servicesChildService = servicesChildService;
        this.openMetadataConfig = openMetadataConfig;
    }

    @GetMapping("/statusCheck")
    public Integer statusCheck() {
        return 200;
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
        var targetService = servicesService.getServices(upsertHistories.getFirst().getServiceID());
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
        var targetService = servicesService.getServices(responseTime.getFirst().getServiceID());
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

    @PostMapping("/databaseService")
    public void databaseService(@RequestBody String requestData) {
        /**
         *
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
        // TestConnection
        var rootNode = getJsonNode(requestData);
        var entityId = UUID.fromString(rootNode.get(ENTITY_ID.getName()).asText());
        var entity = getJsonNode(rootNode.get(ENTITY.getName()).asText());
        var entityType = rootNode.get(ENTITY_TYPE.getName()).asText();
        var eventType = rootNode.get(EVENT_TYPE.getName()).asText();
        var timeStamp = rootNode.get(TIMESTAMP.getName()).asLong();
        try {
            if (openMetadataConfig.getSaveEntityType().getServices().contains(entityType)) {
                saveServices(entity, eventType);
            } else if (openMetadataConfig.getSaveEntityType().getConnect().contains(entityType)) {
                saveConnection(entity, eventType, timeStamp);
                var serviceName = entity.get(REQUEST.getName()).get(SERVICE_NAME.getName()).asText();
                var services = servicesService.getServices(serviceName);
                if (services != null && entity.get(STATUS.getName()) != null) {
                    services = services.toBuilder()
                            .connectionStatus(stringToBoolean(entity.get(STATUS.getName()).asText()))
                            .build();
                    servicesService.saveServices(services);
                }
            }

            if (openMetadataConfig.getSaveEntityType().getHistory().contains(entityType)) {
                saveHistory(entityId, entity, entityType, eventType);
            }

            if (openMetadataConfig.getSaveEntityType().getServicesChild().contains(entityType)) {
                saveServicesChild(entity, entityType, eventType);
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw e;
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

    public Map<String, Object> newParseJson(String jsonString) {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConnection(JsonNode entity, String eventType, Long timeStamp) {
        var entityId = UUID.fromString(entity.get(ID.getName()).asText());

        var serviceName = entity.get(REQUEST.getName()).get(SERVICE_NAME.getName()).asText();
        var services = servicesService.getServices(serviceName);
        var serviceId = services != null ? services.getEntityID() : null;
        var serviceConnect = connectService.getServicesConnect(entityId);

        var instant = Instant.ofEpochMilli(timeStamp);
        var timestamp = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        if (serviceConnect == null) {
            serviceConnect = ServicesConnect.builder()
                    .entityID(entityId)
                    .serviceName(serviceName)
                    .serviceID(serviceId)
                    .startTimestamp(timestamp)
                    .build();
            connectService.saveConnect(serviceConnect);
        }

        if (serviceConnect != null && eventType.equalsIgnoreCase(ENTITY_DELETED.getName())) {
            serviceConnect = serviceConnect.toBuilder()
                    .serviceID(serviceId)
                    .endTimestamp(serviceConnect.getEndTimestamp() == null ?
                            timestamp :
                            serviceConnect.getEndTimestamp().isBefore(timestamp) ?
                                    timestamp : serviceConnect.getEndTimestamp())
                    .build();
            connectService.saveConnect(serviceConnect);
        }
    }

    public void saveServices(JsonNode entity, String eventType) {
        var serviceId = UUID.fromString(entity.get(ID.getName()).asText());
        var services = servicesService.getServices(serviceId);
        if (services == null) {
            var serviceName = entity.get("name").asText();
            // todo createdAt이 이 localDateTime이 아닌, entity에 있는 time을 이용해야한다.
            var service = Services.builder()
                    .entityID(serviceId)
                    .name(serviceName)
                    .createdAt(LocalDateTime.now())
                    .databaseType(entity.get(SERVICE_TYPE.getName()).asText())
                    .ownerName(entity.get(OWNER.getName()).get(NAME.getName()).asText())
                    .connectionStatus(false)
                    .build();

            servicesService.saveServices(service);
        } else {
            if (eventType.equalsIgnoreCase(ENTITY_DELETED.getName())) {
                var oldServices = servicesService.getServices(serviceId);
                var newServices = oldServices.toBuilder()
                        .deleted(true)
                        .build();

                servicesService.saveServices(newServices);
            }
        }
    }

    public void saveServicesChild(JsonNode entity, String entityType, String eventType) {
        var entityId = UUID.fromString(entity.get(ID.getName()).asText());
        var servicesChild = servicesChildService.getServicesChild(entityId);

        servicesChild = servicesChild != null ?
                eventType.equalsIgnoreCase(ENTITY_DELETED.getName()) ?
                        servicesChild.toBuilder()
                                .deleted(true)
                                .build() :
                        servicesChild :
                ServicesChild.builder()
                        .entityID(entityId)
                        .entityName(entity.get(NAME.getName()).asText())
                        .serviceID(UUID.fromString(entity.get(SERVICE.getName()).get(ID.getName()).asText()))
                        .serviceName(entity.get(SERVICE.getName()).get(NAME.getName()).asText())
                        .entityType(entityType)
                        .createdAt(
                                LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(entity.get(UPDATED_AT.getName()).asLong()),
                                        ZoneId.of("UTC")
                                ))
                        .fullyQualifiedName(entity.get(FULLY_QUALIFIED_NAME.getName()).asText())
                        .build();

        servicesChildService.saveServicesChild(servicesChild);
    }

    public void saveHistory(UUID entityId, JsonNode entity, String entityType, String eventType) {
        if (entityType.equalsIgnoreCase(WORKFLOW.getName()) && eventType.equalsIgnoreCase(ENTITY_CREATE.getName()))
            return;

        UUID servicesId;
        String fullyQualifiedName;

        if (entityType.equalsIgnoreCase(WORKFLOW.getName())) {
            var services = servicesService.getServices(entity.get(REQUEST.getName())
                    .get(SERVICE_NAME.getName()).asText());
            servicesId = services.getEntityID();
            eventType = "connection" + (stringToBoolean(entity.get(STATUS.getName()).asText()) ? "Success" : "Fail");
            fullyQualifiedName = services.getName();
        } else {
            servicesId = UUID.fromString(entity.get(SERVICE.getName()) == null ?
                    entity.get(ID.getName()).asText() :
                    entity.get(SERVICE.getName()).get(ID.getName()).asText());
            fullyQualifiedName = entity.get(FULLY_QUALIFIED_NAME.getName()).asText();
        }

        // todo createdAt이 이 localDateTime이 아닌, entity에 있는 time을 이용해야한다.
        var history = ServicesHistory.builder()
                .entityID(entityId)
                .serviceID(servicesId)
                .event(eventType)
                .updatedAt(LocalDateTime.now())
                .fullyQualifiedName(fullyQualifiedName)
                .build();
        historyService.saveServiceHistory(history);
    }

    public JsonNode getJsonNode(String jsonStr) {
        var mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean stringToBoolean(String booleanStr) {
        return booleanStr.equalsIgnoreCase("successful");
    }

    // old
    public void saveConnection(Map<String, Object> requestData) throws NoSuchElementException { // todo exception 처리 필요
        var entity = newParseJson(requestData.get(ENTITY.getName()).toString());
        var eventType = requestData.get(EVENT_TYPE.getName()).toString();

        var serviceName = ((Map<String, Object>) entity.get("request")).get("serviceName").toString();
        var services = servicesService.getServices(serviceName);
        var serviceId = services != null ? services.getEntityID() : null;
        var entityId = UUID.fromString(requestData.get(ENTITY_ID.getName()).toString());
        var serviceConnect = connectService.getServicesConnect(entityId);
        var instant = Instant.ofEpochMilli((Long) requestData.get("timestamp"));
        var timestamp = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        if (serviceConnect == null) {
            serviceConnect = ServicesConnect.builder()
                    .entityID(entityId)
                    .serviceName(serviceName)
                    .serviceID(serviceId)
                    .startTimestamp(timestamp)
                    .build();
            connectService.saveConnect(serviceConnect);
        }

        if (serviceConnect != null && eventType.equalsIgnoreCase(ENTITY_DELETED.getName())) {
            var mapper = new ObjectMapper();
            try {
                JsonNode rootNode = mapper.readTree(requestData.toString());
                JsonNode changeNode = rootNode.get(CHANGE_DESCRIPTION.getName());
                JsonNode fieldsAddedNode = changeNode.get(FIELDS_ADDED.getName());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                System.out.println("error");
            }
            serviceConnect = serviceConnect.toBuilder()
                    .serviceID(serviceId)
                    .endTimestamp(serviceConnect.getEndTimestamp() == null ?
                            timestamp :
                            serviceConnect.getEndTimestamp().isBefore(timestamp) ?
                                    timestamp : serviceConnect.getEndTimestamp())
                    .build();
            connectService.saveConnect(serviceConnect);
        }
    }


}


