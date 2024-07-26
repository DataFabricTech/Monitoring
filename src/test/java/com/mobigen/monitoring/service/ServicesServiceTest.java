package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.enums.ConnectionStatus;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServicesServiceTest {
    @Autowired
    private ServicesService servicesService;

    @Autowired
    private ServicesRepository servicesRepository;

    @AfterEach
    void tearDown() {
        servicesRepository.deleteAll();
    }

    @DisplayName("countByConnectionStatusIsConnectedTest - 기본 값 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectedDeleteTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsConnected());
        });
    }

    @DisplayName("countByConnectionStatusIsConnectedTest - Zero 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectedDeleteZeroTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(0, servicesService.countByConnectionStatusIsConnected());
        });
    }

    @DisplayName("countByConnectionStatusIsConnectedTest - 혼합 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectedDeleteCombinationTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsConnected());
        });
    }

    @DisplayName("countByConnectionStatusIsDisConnectedTest - 기본 값 제공 - 성공")
    @Test
    void countByConnectionStatusIsDisconnectedDeleteTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsDisconnected());
        });
    }

    @DisplayName("countByConnectionStatusIsDisconnectedTest - Zero 제공 - 성공")
    @Test
    void countByConnectionStatusIsDisconnectedDeleteZeroTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(0, servicesService.countByConnectionStatusIsDisconnected());
        });
    }

    @DisplayName("countByConnectionStatusIsDisconnectedTest - 혼합 제공 - 성공")
    @Test
    void countByConnectionStatusIsDisconnectedDeleteCombinationTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsDisconnected());
        });
    }

    @DisplayName("countByConnectionStatusIsConnectErrorTest - 기본 값 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectErrorDeleteTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECT_ERROR)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECT_ERROR)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsConnectError());
        });
    }

    @DisplayName("countByConnectionStatusIsConnectErrorTest - Zero 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectErrorDeleteZeroTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECT_ERROR)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(0, servicesService.countByConnectionStatusIsConnectError());
        });
    }

    @DisplayName("countByConnectionStatusIsConnectErrorTest - 혼합 제공 - 성공")
    @Test
    void countByConnectionStatusIsConnectErrorDeleteCombinationTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.DISCONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECT_ERROR)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECT_ERROR)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.countByConnectionStatusIsConnectError());
        });
    }

    @DisplayName("getServicesCount - 기본 값 제공 - 성공")
    @Test
    void getServicesCountTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.getServicesCount());
        });
    }

    @DisplayName("getServicesCount - null 제공 - 성공")
    @Test
    void getServicesCountNullTest() {
        assertDoesNotThrow(() -> {
            assertEquals(0, servicesService.getServicesCount());
        });
    }

    @DisplayName("getServicesCount - 삭제 값 제공 - 성공")
    @Test
    void getServicesCountDeletedTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.getServicesCount());
        });
    }

    @DisplayName("getServicesList - 기본 값 제공 - 성공")
    @Test
    void getServicesList() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(1, servicesService.getServicesList().size());
        });
    }

    @DisplayName("getServicesList - null 값 제공 - 성공")
    @Test
    void getServicesNullList() {
        assertDoesNotThrow(() -> assertEquals(0, servicesService.getServicesList().size()));
    }

    @DisplayName("getServicesList - 삭제 값 제공 - 성공")
    @Test
    void getServicesListTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());
            serviceDTOs.add(ServiceDTO.builder().serviceID(UUID.randomUUID())
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .deleted(true)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertEquals(2, servicesService.getServicesList().size());
        });
    }

    @DisplayName("getServices - 기본 값 제공 - 성공")
    @Test
    void getServicesTest() {
        assertDoesNotThrow(() -> {
            List<ServiceDTO> serviceDTOs = new ArrayList<>();
            var serviceId = UUID.randomUUID();
            serviceDTOs.add(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .connectionStatus(ConnectionStatus.CONNECTED)
                    .createdAt(LocalDateTime.now())
                    .build());

            servicesService.saveServices(serviceDTOs);


            assertTrue(servicesService.getServices(serviceId).isPresent());
            assertEquals("testService2", servicesService.getServices(serviceId).get().getName());
        });
    }

    @DisplayName("getServices - null 값 제공 - 성공")
    @Test
    void getServicesNullTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();

            assertFalse(servicesService.getServices(serviceId).isPresent());
        });
    }
}