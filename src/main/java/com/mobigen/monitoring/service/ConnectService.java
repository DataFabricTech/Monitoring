package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.ServicesConnectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConnectService {
    OpenMetadataConfig openMetadataConfig;
    ServicesConnectRepository servicesConnectRepository;
    public ConnectService(OpenMetadataConfig openMetadataConfig, ServicesConnectRepository servicesConnectRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesConnectRepository = servicesConnectRepository;
    }

    public List<Object[]> getServiceConnectList() {
        return servicesConnectRepository.findTopAverageConnectResponseTimes(
                PageRequest.of(openMetadataConfig.getPageableConfig().getConnect().getSize(),
                        openMetadataConfig.getPageableConfig().getConnect().getPage()));
    }

    public List<ServicesConnect> getServiceConnectList(UUID serviceID) {
        return servicesConnectRepository.findTopByOrderByConnectResponseTimeDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getConnect().getSize(),
                        openMetadataConfig.getPageableConfig().getConnect().getPage()));
    }

    public ServicesConnect getServiceConnect(UUID entityID) {
        return servicesConnectRepository.findById(entityID).orElse(null);
    }

    public void saveConnect(ServicesConnect entity) { servicesConnectRepository.save(entity);}

    public void runConnection() {

    }
}
