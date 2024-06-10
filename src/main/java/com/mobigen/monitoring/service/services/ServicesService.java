package com.mobigen.monitoring.service.services;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.repository.ServicesRepository;
import com.mobigen.monitoring.utils.RestAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ServicesService implements Services {
    OpenMetadataConfig openMetadataConfig;
    RestAPI restAPI;
    ServicesRepository servicesRepository;

    public ServicesService(OpenMetadataConfig openMetadataConfig, RestAPI restAPI, ServicesRepository servicesRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.restAPI = restAPI;
        this.servicesRepository = servicesRepository;
    }

    /**
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * The number of items depend on config (Default is 5)
     */
    @Override
    public Map<String, String> getRecentChangeServices() {
        var services = servicesRepository.findTopByOrderByUpdatedAtDesc(openMetadataConfig.getNumberOf().getRecentChange());
        return null;
    }

    /**
     *
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     * @return
     */
    @Override
    public Map<String, String> ServiceHistory() {
        var services = servicesRepository.findTopByOrderByUpdatedAtDesc(openMetadataConfig.getNumberOf().getRecentChange());
        return null;
    }

    @Override
    public Map<String, String> ServiceHistory(String serviceID) {
        return null;
    }

    @Override
    public Map<String, Integer> getServiceTypeCount() {
        return null;
    }

    @Override
    public void runConnection() {

    }

    @Override
    public T getEntity() {
        return null;
    }
}
