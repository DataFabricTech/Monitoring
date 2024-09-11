package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.HistoryDTO;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.EventType.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HistoryServiceTest {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ServicesHistoryRepository servicesHistoryRepository;
    @Autowired
    private ServicesRepository servicesRepository;

    @AfterEach
    void tearDown() {
        servicesHistoryRepository.deleteAll();
    }


    @DisplayName("getServiceHistoriesTest - 기본 값 제공 - 성공")
    @Test
    void getServiceHistoriesAndSaveTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<HistoryDTO> historyDTOs = new ArrayList<>();
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_CREATE)
                    .serviceID(serviceId)
                    .build());

            historyService.saveHistory(historyDTOs);


            var whenHistoryDTOs = historyService.getServiceHistories(0, 100);

            assertEquals(1, whenHistoryDTOs.size());
        });
    }

    @DisplayName("getServiceHistoriesTest - 정렬 Test - 성공")
    @Test
    void getServiceHistoriesDescTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<HistoryDTO> historyDTOs = new ArrayList<>();
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_UPDATED)
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_CREATE)
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_DELETED)
                    .serviceID(serviceId)
                    .build());

            historyService.saveHistory(historyDTOs);


            var whenHistoryDTOs = historyService.getServiceHistories(0, 100);
            assertEquals(SERVICE_DELETED, whenHistoryDTOs.getFirst().getEvent());
            assertEquals(SERVICE_UPDATED, whenHistoryDTOs.get(1).getEvent());
            assertEquals(SERVICE_CREATE, whenHistoryDTOs.getLast().getEvent());
        });
    }

    @Test
    void GetServiceHistoriesTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            var serviceId2 = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId2)
                    .name("testService")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<HistoryDTO> historyDTOs = new ArrayList<>();
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_UPDATED)
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_CREATE)
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_CREATE)
                    .serviceID(serviceId2)
                    .build());

            historyService.saveHistory(historyDTOs);

            assertEquals(2, historyService.getServiceHistories(serviceId, 0, 100).size());
            assertEquals(1, historyService.getServiceHistories(serviceId2, 0, 100).size());
        });
    }

    @Test
    void getServiceConnectionHistories() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<HistoryDTO> historyDTOs = new ArrayList<>();
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_DELETED)
                    .description(SERVICE_DELETED.name())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(2).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_UPDATED)
                    .description(CONNECTED.getName())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_UPDATED)
                    .description(CONNECTED.getName())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(4).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_UPDATED)
                    .description(CONNECT_ERROR.getName())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(5).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(SERVICE_CREATE)
                    .description(SERVICE_CREATE.name())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(6).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(CONNECTION_CHECK)
                    .description(CONNECTION_CHECK.name())
                    .serviceID(serviceId)
                    .build());
            historyDTOs.add(HistoryDTO.builder()
                    .updateAt(LocalDateTime.now().plusSeconds(7).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .event(UNKNOWN)
                    .description(UNKNOWN.getName())
                    .serviceID(serviceId)
                    .build());

            historyService.saveHistory(historyDTOs);

            assertEquals(7, historyService.getServiceHistories(serviceId, 0, 100).size());
            assertEquals(3, historyService.getServiceConnectionHistories(serviceId, 0, 100).size());
        });
    }
}