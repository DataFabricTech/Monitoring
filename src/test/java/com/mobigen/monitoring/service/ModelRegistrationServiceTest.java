package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void saveAndGetModelRegistrationsTest() {
        assertDoesNotThrow(()-> {
            var serviceId = UUID.randomUUID();
            servicesRepository.save(ServiceDTO.builder().serviceID(serviceId)
                    .name("testService2")
                    .serviceType("testServiceType")
                    .createdAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());

            List<ModelRegistration> modelRegistrationList = new ArrayList<>();
            modelRegistrationList.add(ModelRegistration.builder()
                    .serviceId(serviceId)
                    .name("testModel")
                    .omModelCount(1)
                    .modelCount(2)
                    .build());

            modelRegistrationService.saveModelRegistrations(modelRegistrationList);

            assertEquals(2, modelRegistrationService.getModelRegistrations(0, 1).getFirst().getModelCount());
        });
    }
}