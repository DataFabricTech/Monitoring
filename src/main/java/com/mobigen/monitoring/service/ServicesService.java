package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.dto.Services;
import com.mobigen.monitoring.dto.ServicesChange;
import com.mobigen.monitoring.dto.ServicesEvent;
import com.mobigen.monitoring.repository.ServicesChangeRepository;
import com.mobigen.monitoring.repository.ServicesEventRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class ServicesService {
    OpenMetadataConfig openMetadataConfig;
    ServicesRepository servicesRepository;
    ServicesChangeRepository servicesChangeRepository;
    ServicesEventRepository servicesEventRepository;

    public ServicesService(OpenMetadataConfig openMetadataConfig, ServicesRepository servicesRepository,
                           ServicesChangeRepository servicesChangeRepository, ServicesEventRepository servicesEventRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesRepository = servicesRepository;
        this.servicesChangeRepository = servicesChangeRepository;
        this.servicesEventRepository = servicesEventRepository;
    }

    public Long countByConnectionStatusIsTrue() {
        return servicesRepository.countByConnectionStatusIsTrue();
    }

    public Long getServicesCount() {
        return servicesRepository.count();
    }

    public Services getServices(UUID serviceID) {
        return servicesRepository.findServicesByServiceID(serviceID);
    }
}
