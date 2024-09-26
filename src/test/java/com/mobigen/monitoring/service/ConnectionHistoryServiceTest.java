package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.CONNECT_ERROR;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConnectionHistoryServiceTest {
    @Autowired
    private ConnectionHistoryService connectionHistoryService;
    @Autowired
    private ServicesRepository servicesRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @DisplayName("getConnectionHistoriesResponse - default - 성공")
    @Test
    void getConnectionHistoriesResponseDefaultTest() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);

            assertEquals(3, connectionHistoryService.getConnectionHistoriesResponse(PageRequest.of(0, 10)).size());
            assertEquals(uuid, connectionHistoryService.getConnectionHistoriesResponse(PageRequest.of(0, 10)).getFirst().serviceId());
        });
    }

    @DisplayName("getConnectionHistoriesResponse - null value - 성공")
    @Test
    void getConnectionHistoriesResponseNullValueTest2() {
        assertDoesNotThrow(() ->
                assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(PageRequest.of(0, 10)).size())
        );
    }

    @DisplayName("getConnectionHistoriesResponse - serviceId - default - 성공")
    @Test
    void getConnectionHistoriesResponseWithServiceIdDefault() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);

            assertEquals(3, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            assertEquals(uuid, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).getFirst().serviceId());
        });
    }

    @DisplayName("getConnectionHistoriesResponse - serviceId - null,wrong - 성공")
    @Test
    void getConnectionHistoriesResponseWithServiceIdWithNull() {
        assertDoesNotThrow(() -> {
                    assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(null, PageRequest.of(0, 10)).size());
                    assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(UUID.randomUUID(), PageRequest.of(0, 10)).size());
                }
        );
    }

    @DisplayName("getConnectionHistories - default - 성공")
    @Test
    void getConnectionHistoriesDefaultTest() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid2)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid2)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);

            assertEquals(2, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            assertEquals(uuid, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).getFirst().serviceId());
        });
    }

    @DisplayName("getConnectionHistories - Null, Wrong - 성공")
    @Test
    void getConnectionHistoriesNullAndWrongServiceIdTest() {
        assertDoesNotThrow(() -> {
            assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(null, PageRequest.of(0, 10)).size());
            assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(UUID.randomUUID(), PageRequest.of(0, 10)).size());
        });

    }

    @DisplayName("saveConnectionHistory - save one - 성공")
    @Test
    void saveConnectionHistoryDefault() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);
            assertEquals(1, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            assertEquals(uuid, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).getFirst().serviceId());
        });
    }

    @DisplayName("saveConnectionHistory - save null - 성공")
    @Test
    void saveConnectionHistoryNull() {
        assertDoesNotThrow(() -> {
            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);
            assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(PageRequest.of(0, 10)).size());
        });
    }

    /**
     * todo
     * 2. least two type of cut off days
     * 3. minus value insert error - fail test
     */
    @DisplayName("deleteConnectionHistory - default - 성공")
    @Test
    void deleteConnectionHistoryDefault() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusSeconds(10).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);
            assertEquals(1, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());

            connectionHistoryService.deleteConnectionHistory(0);

            assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("deleteConnectionHistory - diff cutoff - 성공")
    @Test
    void deleteConnectionHistoryTwoType() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusDays(50).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusDays(20).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());

            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);
            assertEquals(3, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            connectionHistoryService.deleteConnectionHistory(99);
            assertEquals(2, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            connectionHistoryService.deleteConnectionHistory(40);
            assertEquals(1, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            connectionHistoryService.deleteConnectionHistory(10);
            assertEquals(0, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
        });
    }

    @DisplayName("deleteConnectionHistory - minus value - 성공")
    @Test
    void deleteConnectionHistoryMinusDays() {
        assertDoesNotThrow(() -> {
            var uuid = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder()
                    .serviceID(uuid)
                    .name("primaryService")
                    .serviceType("MYSQL")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
            List<ConnectionHistoryDTO> connectionHistoryDTOList = new ArrayList<>();
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().minusDays(100).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());
            connectionHistoryDTOList.add(ConnectionHistoryDTO.builder()
                    .updatedAt(LocalDateTime.now().plusDays(100).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .serviceID(uuid)
                    .connectionStatus(CONNECT_ERROR)
                    .build());


            connectionHistoryService.saveConnectionHistory(connectionHistoryDTOList);
            assertEquals(2, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
            connectionHistoryService.deleteConnectionHistory(-1);
            assertEquals(1, connectionHistoryService.getConnectionHistoriesResponse(uuid, PageRequest.of(0, 10)).size());
        });
    }
}