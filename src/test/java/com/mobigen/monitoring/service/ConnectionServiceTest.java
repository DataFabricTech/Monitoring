package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.ConnectionDTO;
import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.repository.ModelRegistrationRepository;
import com.mobigen.monitoring.repository.ServicesConnectResponseRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConnectionServiceTest {
    private final Utils utils = new Utils();
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private ServicesConnectResponseRepository servicesConnectResponseRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ModelRegistrationRepository modelRegistrationRepository;

    @AfterEach
    public void tearDown() {
        servicesConnectResponseRepository.deleteAll();
        servicesRepository.deleteAll();
        modelRegistrationRepository.deleteAll();
    }


    @Test
    void setDequeTest() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

        connectionService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);
        var servicesQueueField = ConnectionService.class.getDeclaredField("servicesQueue");
        var historiesQueueField = ConnectionService.class.getDeclaredField("historiesQueue");
        var connectsQueueField = ConnectionService.class.getDeclaredField("connectsQueue");
        var modelRegistrationQueueField = ConnectionService.class.getDeclaredField("modelRegistrationQueue");

        servicesQueueField.setAccessible(true);
        historiesQueueField.setAccessible(true);
        connectsQueueField.setAccessible(true);
        modelRegistrationQueueField.setAccessible(true);


        assertSame(servicesQueue, servicesQueueField.get(connectionService));
        assertSame(historiesQueue, historiesQueueField.get(connectionService));
        assertSame(connectsQueue, connectsQueueField.get(connectionService));
        assertSame(modelRegistrationQueue, modelRegistrationQueueField.get(connectionService));
    }

    @DisplayName("saveConnects - 기본 값 제공 - 성공")
    @Test
    void saveConnectsTest() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionDTO> connectList = new ArrayList<>();

            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .executeBy("testUser")
                    .serviceID(uuid)
                    .build());

            connectionService.saveConnections(connectList);

            assertEquals(1, servicesConnectResponseRepository.findAll().size());
            assertEquals(uuid, servicesConnectResponseRepository.findAll().getFirst().getServiceID());
        });
    }

    @DisplayName("saveConnects - 2개 이상의 값 제공 - 성공")
    @Test
    void saveConnectsUpperTwoElementsTest() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            var service = ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();
            servicesRepository.save(service);

            List<ConnectionDTO> connectList = new ArrayList<>();
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .executeBy("testUser")
                    .serviceID(uuid)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .executeBy("testUser2")
                    .serviceID(uuid)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .executeBy("testUser3")
                    .serviceID(uuid)
                    .build());


            connectionService.saveConnections(connectList);


            assertEquals(3, servicesConnectResponseRepository.findAll().size());
        });
    }

    @DisplayName("saveConnects - 0개 넣기 제공 - 성공")
    @Test
    void saveConnectsEmptyElementsTest() {
        assertDoesNotThrow(() -> {
            List<ConnectionDTO> connectList = new ArrayList<>();

            connectionService.saveConnections(connectList);

            assertEquals(0, servicesConnectResponseRepository.findAll().size());
        });
    }

    @DisplayName("saveConnects - null 제공 - 실패")
    @Test
    void saveConnectsNullElementsTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            connectionService.saveConnections(null);
            assertEquals(0, servicesConnectResponseRepository.findAll().size());
        });
    }

    @DisplayName("getServiceConnectResponseTimeAscList - 기본값 제공 - 성공")
    @Test
    void getServiceConnectResponseTimeAscListTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceList = new ArrayList<>();
            var uuid1 = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid1)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid2)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid3)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            servicesRepository.saveAll(serviceList);

            List<ConnectionDTO> connectList = new ArrayList<>();
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceID(uuid1)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceID(uuid2)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceID(uuid3)
                    .build());


            connectionService.saveConnections(connectList);


            var resultList = connectionService.getConnectionResponseTime(PageRequest.of(0, 10, Sort.by("executeAt").ascending()));

            assertEquals(uuid2, resultList.get(0).serviceId());
            assertEquals(uuid1, resultList.get(1).serviceId());
            assertEquals(uuid3, resultList.get(2).serviceId());
        });
    }

    @DisplayName("getServiceConnectResponseTimeDescListTest - 기본 값 제공 - 성공")
    @Test
    void getServiceConnectResponseTimeDescListTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceList = new ArrayList<>();
            var uuid1 = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid1)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid2)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            serviceList.add(ServiceDTO.builder()
                    .serviceID(uuid3)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            servicesRepository.saveAll(serviceList);

            List<ConnectionDTO> connectList = new ArrayList<>();
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceID(uuid1)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceID(uuid2)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceID(uuid3)
                    .build());


            connectionService.saveConnections(connectList);

            var resultList = connectionService.getConnectionResponseTime(PageRequest.of(0, 10, Sort.by("executeAt").descending()));
            assertEquals(uuid3, resultList.get(0).serviceId());
            assertEquals(uuid1, resultList.get(1).serviceId());
            assertEquals(uuid2, resultList.get(2).serviceId());
        });
    }

    @DisplayName("getServiceConnectResponseTimeTest - 기본 값 제공 - 성공")
    @Test
    void getServiceConnectResponseTimeTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            var serviceId2 = UUID.randomUUID();

            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService1")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId2)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionDTO> connectList = new ArrayList<>();
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(3L)
                    .executeBy("testUser4")
                    .serviceID(serviceId2)
                    .build());


            connectionService.saveConnections(connectList);

            assertEquals(3, connectionService.getConnectionResponseTime(serviceId, PageRequest.of(0, 10, Sort.by("queryExecutionTime").descending())).size());
            assertEquals(1, connectionService.getConnectionResponseTime(serviceId2, PageRequest.of(0, 10, Sort.by("queryExecutionTime").descending())).size());
        });
    }

    /**
     * connected, disconnected, error test
     */
    @DisplayName("getDBItemsTest - 기본 값 제공 - 성공")
    @Test
    void getDBItemsTest() {
        assertDoesNotThrow(() -> {
            var serviceJsonStr = "{\"id\":\"415a9c2d-2ec2-4a93-b92f-063057dca6e1\",\"name\":\"testFQN\"," +
                    "\"fullyQualifiedName\":\"testFQN\",\"serviceType\":\"H2\",\"description\":\"\"," +
                    "\"connection\":{\"config\":{\"type\":\"H2\",\"scheme\":\"mysql+pymysql\",\"username\":\"sa\"," +
                    "\"authType\":{\"password\":\"password\"},\"hostPort\":\"mem:testdb\"," +
                    "\"supportsMetadataExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                    "\"supportsQueryComment\":true}},\"version\":0.1,\"updatedAt\":1720059708067,\"updatedBy\":\"admin\"," +
                    "\"href\":\"http://192.168.106.104:8585/api/v1/services/databaseServices/415a9c2d-2ec2-4a93-b92f-063057dca6e1\"," +
                    "\"deleted\":false}";

            var serviceJson = utils.getJsonNode(serviceJsonStr);

            ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectionService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectionService.getDBItems(serviceJson, 10, "tester");
            List<ServiceDTO> servicesList = servicesQueue.stream()
                    .map(GenericWrapper::getObject)
                    .toList();

            assertEquals(CONNECTED, servicesList.getFirst().getConnectionStatus());
        });
    }

    @DisplayName("getDBItemsTest - 잘못된 url 값 제공 - 실패")
    @Test
    void getDBItemsDisconnectedTest() {
        assertDoesNotThrow(() -> {
            var serviceJsonStr = "{\"id\":\"415a9c2d-2ec2-4a93-b92f-063057dca6e1\",\"name\":\"testFQN\"," +
                    "\"fullyQualifiedName\":\"testFQN\",\"serviceType\":\"H2\",\"description\":\"\"," +
                    "\"connection\":{\"config\":{\"type\":\"H2\",\"scheme\":\"mysql+pymysql\",\"username\":\"sa\"," +
                    "\"authType\":{\"password\":\"password\"},\"hostPort\":\"\"," +
                    "\"supportsMetadataExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                    "\"supportsQueryComment\":true}},\"version\":0.1,\"updatedAt\":1720059708067,\"updatedBy\":\"admin\"," +
                    "\"href\":\"http://192.168.106.104:8585/api/v1/services/databaseServices/415a9c2d-2ec2-4a93-b92f-063057dca6e1\"," +
                    "\"deleted\":false}";

            var serviceJson = utils.getJsonNode(serviceJsonStr);

            ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectionService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectionService.getDBItems(serviceJson, 10, "tester");
            List<ServiceDTO> servicesList = servicesQueue.stream()
                    .map(GenericWrapper::getObject)
                    .toList();

            assertEquals(DISCONNECTED, servicesList.getFirst().getConnectionStatus());
        });
    }

    @DisplayName("getDBItemsTest - 잘못된 권한 값 제공 - 실패")
    @Test
    void getDBItemsConnectErrorTest() {
        assertDoesNotThrow(() -> {
            var serviceJsonStr = "{\"id\":\"415a9c2d-2ec2-4a93-b92f-063057dca6e1\",\"name\":\"testFQN\"," +
                    "\"fullyQualifiedName\":\"testFQN\",\"serviceType\":\"H2\",\"description\":\"\"," +
                    "\"connection\":{\"config\":{\"type\":\"H2\",\"scheme\":\"mysql+pymysql\",\"username\":\"wrongUserName\"," +
                    "\"authType\":{\"password\":\"password\"},\"hostPort\":\"mem:testdb\"," +
                    "\"supportsMetadataExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                    "\"supportsQueryComment\":true}},\"version\":0.1,\"updatedAt\":1720059708067,\"updatedBy\":\"admin\"," +
                    "\"href\":\"http://192.168.106.104:8585/api/v1/services/databaseServices/415a9c2d-2ec2-4a93-b92f-063057dca6e1\"," +
                    "\"deleted\":false}";

            var serviceJson = utils.getJsonNode(serviceJsonStr);

            ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectionService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectionService.getDBItems(serviceJson, 10, "tester");
            List<ServiceDTO> servicesList = servicesQueue.stream()
                    .map(GenericWrapper::getObject)
                    .toList();

            assertEquals(CONNECT_ERROR, servicesList.getFirst().getConnectionStatus());
        });
    }

    @DisplayName("getDBItemTest - 지원하지 않는 DBType 제공 처리 - 실패")
    @Test
    void getDBItemUnSupportedDBTypeTest() {
        assertDoesNotThrow(() -> {
            var serviceJsonStr = "{\"id\":\"efd88a2d-bc70-479d-88a1-3719b202b477\",\"name\":\"datamodels\"," +
                    "\"fullyQualifiedName\":\"datamodels\",\"serviceType\":\"Trino\",\"description\":\"\"," +
                    "\"connection\":{\"config\":{\"type\":\"Trino\",\"scheme\":\"trino\"," +
                    "\"username\":\"openmetadata\",\"hostPort\":\"secret\"," +
                    "\"supportsMetadataExtraction\":true,\"supportsUsageExtraction\":true," +
                    "\"supportsLineageExtraction\":true,\"supportsDBTExtraction\":true,\"supportsProfiler\":true," +
                    "\"supportsDatabase\":true,\"supportsQueryComment\":true}},\"version\":0.1," +
                    "\"updatedAt\":1719539122539,\"updatedBy\":\"admin\"," +
                    "\"href\":\"http://192.168.106.104:8585/api/v1/services/databaseServices/efd88a2d-bc70-479d-88a1-3719b202b477\"," +
                    "\"deleted\":false}";

            var serviceJson = utils.getJsonNode(serviceJsonStr);

            ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectionService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);

            connectionService.getDBItems(serviceJson, 10, "tester");
        });
    }

    @DisplayName("getCount - default - 성공")
    @Test
    void getCountDefault() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();

            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(serviceId)
                    .name("testService1")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionDTO> connectList = new ArrayList<>();
            connectList.add(ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceID(serviceId)
                    .build());

            connectionService.saveConnections(connectList);
            assertEquals(1, connectionService.getCount());
        });
    }

    @DisplayName("getCount - empty - 성공")
    @Test
    void getCountEmpty() {
        assertEquals(0, connectionService.getCount());
    }
}