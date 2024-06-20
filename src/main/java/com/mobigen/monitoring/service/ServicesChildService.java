package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesChild;
import com.mobigen.monitoring.repository.ServicesChildRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ServicesChildService {
    OpenMetadataConfig openMetadataConfig;
    ServicesChildRepository servicesChildRepository;

    public ServicesChildService(OpenMetadataConfig openMetadataConfig, ServicesChildRepository servicesChildRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesChildRepository = servicesChildRepository;
    }

    public ServicesChild getServicesChild(UUID entityID) {
        return servicesChildRepository.findServicesChildByEntityID(entityID);
    }

    public List<ServicesChild> getServicesChildren(UUID serviceId) {
        return  servicesChildRepository.findServicesChildrenByServiceID(serviceId);
    }

    public void saveServicesChild(ServicesChild servicesChild) {
        servicesChildRepository.save(servicesChild);
    }
}
