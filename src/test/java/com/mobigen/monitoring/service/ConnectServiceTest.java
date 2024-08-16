package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.ConnectDTO;
import com.mobigen.monitoring.model.dto.HistoryDTO;
import com.mobigen.monitoring.repository.ModelRegistrationRepository;
import com.mobigen.monitoring.repository.ServicesConnectRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import com.mobigen.monitoring.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConnectServiceTest {
    private final Utils utils = new Utils();
    @Autowired
    private ConnectService connectService;
    @Autowired
    private ServicesConnectRepository servicesConnectRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ModelRegistrationRepository modelRegistrationRepository;

    @BeforeEach
    public void setUp() {
        servicesConnectRepository.deleteAll();
        servicesRepository.deleteAll();
        modelRegistrationRepository.deleteAll();
    }

    @Test
    void setDequeTest() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();


        connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);
        var servicesQueueField = ConnectService.class.getDeclaredField("servicesQueue");
        var historiesQueueField = ConnectService.class.getDeclaredField("historiesQueue");
        var connectsQueueField = ConnectService.class.getDeclaredField("connectsQueue");
        var modelRegistrationQueueField = ConnectService.class.getDeclaredField("modelRegistrationQueue");

        servicesQueueField.setAccessible(true);
        historiesQueueField.setAccessible(true);
        connectsQueueField.setAccessible(true);
        modelRegistrationQueueField.setAccessible(true);


        assertSame(servicesQueue, servicesQueueField.get(connectService));
        assertSame(historiesQueue, historiesQueueField.get(connectService));
        assertSame(connectsQueue, connectsQueueField.get(connectService));
        assertSame(modelRegistrationQueue, modelRegistrationQueueField.get(connectService));
    }

    @DisplayName("saveConnects - 기본 값 제공 - 성공")
    @Test
    void saveConnectsTest() {
        assertDoesNotThrow(() -> {
            List<ConnectDTO> connectList = new ArrayList<>();
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .executeBy("testUser")
                    .serviceName("test")
                    .build());


            connectService.saveConnects(connectList);


            assertEquals(1, servicesConnectRepository.findAll().size());
            assertEquals("test", servicesConnectRepository.findAll().get(0).getServiceName());
        });
    }

    @DisplayName("saveConnects - 2개 이상의 값 제공 - 성공")
    @Test
    void saveConnectsUpperTwoElementsTest() {
        assertDoesNotThrow(() -> {
            List<ConnectDTO> connectList = new ArrayList<>();
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .executeBy("testUser")
                    .serviceName("test")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .executeBy("testUser2")
                    .serviceName("test2")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .executeBy("testUser3")
                    .serviceName("test3")
                    .build());


            connectService.saveConnects(connectList);


            assertEquals(3, servicesConnectRepository.findAll().size());
        });
    }

    @DisplayName("saveConnects - 0개 넣기 제공 - 성공")
    @Test
    void saveConnectsEmptyElementsTest() {
        assertDoesNotThrow(() -> {
            List<ConnectDTO> connectList = new ArrayList<>();


            connectService.saveConnects(connectList);


            assertEquals(0, servicesConnectRepository.findAll().size());
        });
    }

    @DisplayName("saveConnects - null 제공 - 실패")
    @Test
    void saveConnectsNullElementsTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            connectService.saveConnects(null);
            assertEquals(0, servicesConnectRepository.findAll().size());
        });
    }

    @DisplayName("getServiceConnectResponseTimeAscList - 기본값 제공 - 성공")
    @Test
    void getServiceConnectResponseTimeAscListTest() {
        assertDoesNotThrow(() -> {
            List<ConnectDTO> connectList = new ArrayList<>();
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10))
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceName("test")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20))
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceName("test2")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceName("test3")
                    .build());


            connectService.saveConnects(connectList);


            var resultList = connectService.getServiceConnectResponseTimeAscList(0, 10);
            assertEquals("test", resultList.get(0).getServiceName());
            assertEquals("test2", resultList.get(1).getServiceName());
            assertEquals("test3", resultList.get(2).getServiceName());
        });
    }

    @DisplayName("getServiceConnectResponseTimeDescListTest - 기본 값 제공 - 성공")
    @Test
    void getServiceConnectResponseTimeDescListTest() {
        assertDoesNotThrow(() -> {
            List<ConnectDTO> connectList = new ArrayList<>();
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10))
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceName("test")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20))
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceName("test2")
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceName("test3")
                    .build());


            connectService.saveConnects(connectList);


            var resultList = connectService.getServiceConnectResponseTimeDescList(0, 10);
            assertEquals("test3", resultList.get(0).getServiceName());
            assertEquals("test2", resultList.get(1).getServiceName());
            assertEquals("test", resultList.get(2).getServiceName());
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
                    .createdAt(LocalDateTime.now())
                    .build());
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId2)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now())
                    .build());

            List<ConnectDTO> connectList = new ArrayList<>();
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(10))
                    .queryExecutionTime(1L)
                    .executeBy("testUser")
                    .serviceName("test")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now().minusSeconds(20))
                    .queryExecutionTime(2L)
                    .executeBy("testUser2")
                    .serviceName("test2")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .queryExecutionTime(3L)
                    .executeBy("testUser3")
                    .serviceName("test3")
                    .serviceID(serviceId)
                    .build());
            connectList.add(ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .queryExecutionTime(3L)
                    .executeBy("testUser4")
                    .serviceName("test3")
                    .serviceID(serviceId2)
                    .build());


            connectService.saveConnects(connectList);


            assertEquals(3, connectService.getServiceConnectResponseTime(serviceId, 0, 10).size());
            assertEquals(1, connectService.getServiceConnectResponseTime(serviceId2, 0, 10).size());
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
            ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectService.getDBItems(serviceJson, 10, "tester");
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
            ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectService.getDBItems(serviceJson, 10, "tester");
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
            ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);


            connectService.getDBItems(serviceJson, 10, "tester");
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
            ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue = new ConcurrentLinkedDeque<>();
            ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue = new ConcurrentLinkedDeque<>();

            connectService.setDeque(servicesQueue, historiesQueue, connectsQueue, modelRegistrationQueue);

            connectService.getDBItems(serviceJson, 10, "tester");
        });
    }
}