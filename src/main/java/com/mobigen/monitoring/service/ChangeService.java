package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.dto.ServicesChange;
import com.mobigen.monitoring.repository.ServicesChangeRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChangeService {
    OpenMetadataConfig openMetadataConfig;
    ServicesChangeRepository servicesChangeRepository;
    public ChangeService(OpenMetadataConfig openMetadataConfig, ServicesChangeRepository servicesChangeRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesChangeRepository = servicesChangeRepository;
    }

    /**
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * The number of items depend on config (Default is 5)
     */
    public List<ServicesChange> getServiceRecentChange() {
        return servicesChangeRepository.findTopByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getSize(),
                        openMetadataConfig.getPageableConfig().getChange().getPage()));
    }

    public List<ServicesChange> getServiceRecentChange(UUID serviceID) {
        return servicesChangeRepository.findTopByServiceIDOrderByUpdatedAtDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getSize(),
                        openMetadataConfig.getPageableConfig().getChange().getPage()));
    }
}
