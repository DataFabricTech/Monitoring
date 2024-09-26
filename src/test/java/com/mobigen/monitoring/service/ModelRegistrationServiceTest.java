package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.repository.ServicesRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ModelRegistrationServiceTest {
    @Autowired
    private ModelRegistrationService modelRegistrationService;
    @Autowired
    private ServicesRepository servicesRepository;

    @BeforeEach
    void tearDown() {
        modelRegistrationService.deleteAll();
    }

    @Test
    void saveAndGetModelRegistrationsTest() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ModelRegistration> modelRegistrationList = new ArrayList<>();
            modelRegistrationList.add(ModelRegistration.builder()
                    .serviceId(serviceId)
                    .omModelCount(1)
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .modelCount(2)
                    .build());

            modelRegistrationService.saveModelRegistrations(modelRegistrationList);
            assertEquals(2, modelRegistrationService.getModelRegistrations(PageRequest.of(0, 1)).getFirst().modelCount());
            assertEquals(1, modelRegistrationService.getModelRegistrations(PageRequest.of(0, 1)).getFirst().omModelCount());
        });
    }

    @DisplayName("getCount - default - 성공")
    @Test
    void getCountDefault() {
        assertDoesNotThrow(() -> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ModelRegistration> modelRegistrationList = new ArrayList<>();
            modelRegistrationList.add(ModelRegistration.builder()
                    .serviceId(serviceId)
                    .omModelCount(1)
                    .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .modelCount(2)
                    .build());

            modelRegistrationService.saveModelRegistrations(modelRegistrationList);
            assertEquals(1, modelRegistrationService.getCount());
        });
    }


    @DisplayName("getCount - empty - 성공")
    @Test
    void getCountEmpty() {
        assertEquals(0, modelRegistrationService.getCount());
    }
}