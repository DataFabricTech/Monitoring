package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Slf4j
@Service
public class ServicesService {
    final ServicesRepository servicesRepository;

    public ServicesService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }

    public Long countByConnectionStatusIsTrue() {
        return servicesRepository.countByConnectionStatusIsTrueAndDeletedIsFalse();
    }

    public Long countByConnectionStatusIsFalse() {
        return servicesRepository.countByConnectionStatusIsFalseAndDeletedIsFalse();
    }

    public Long getServicesCount() {
        return servicesRepository.countServicesByDeletedIsFalse();
    }

    public List<Services> getServicesList() {
        return servicesRepository.findAll();
    }

    public Services getServices(UUID serviceID) {
        return servicesRepository.findServicesByEntityID(serviceID);
    }

    public void saveServices(JsonNode entity) {
        var serviceId = UUID.fromString(entity.get(ID.getName()).asText());

        var serviceName = entity.get("name").asText();
        var service = Services.builder()
                .entityID(serviceId)
                .name(serviceName)
                .createdAt(LocalDateTime.now())
                .serviceType(entity.get(SERVICE_TYPE.getName()).asText())
                .ownerName(entity.get(UPDATED_BY.getName()).asText())
                .connectionStatus(false)
                .build();

        servicesRepository.save(service);
    }

    public void saveServices(List<Services> servicesList) {
        servicesRepository.saveAll(servicesList);
    }
}
