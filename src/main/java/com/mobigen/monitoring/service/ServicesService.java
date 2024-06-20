package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ServicesService {
    OpenMetadataConfig openMetadataConfig;
    ServicesRepository servicesRepository;

    public ServicesService(OpenMetadataConfig openMetadataConfig, ServicesRepository servicesRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesRepository = servicesRepository;
    }

    public Long countByConnectionStatusIsTrue() {
        return servicesRepository.countByConnectionStatusIsTrueAndDeletedIsFalse();
    }

    public Long getServicesCount() {
        return servicesRepository.countServicesByDeletedIsFalse();
    }

    public Services getServices(UUID serviceID) {
        return servicesRepository.findServicesByEntityID(serviceID);
    }

    public Services getServices(String serviceName) {
        return servicesRepository.findServicesByNameAndDeletedIsFalse(serviceName);
    }

    public void saveServices(Services services) {
        servicesRepository.save(services);
    }
}
