package com.mobigen.monitoring.service.services;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.dto.ServicesChange;
import com.mobigen.monitoring.dto.ServicesConnect;
import com.mobigen.monitoring.dto.ServicesEvent;
import com.mobigen.monitoring.repository.ServicesChangeRepository;
import com.mobigen.monitoring.repository.ServicesConnectRepository;
import com.mobigen.monitoring.repository.ServicesEventRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import com.mobigen.monitoring.utils.RestAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ServicesService implements Services {
    OpenMetadataConfig openMetadataConfig;
    RestAPI restAPI;
    ServicesRepository servicesRepository;
    ServicesChangeRepository servicesChangeRepository;
    ServicesConnectRepository servicesConnectRepository;
    ServicesEventRepository servicesEventRepository;

    public ServicesService(OpenMetadataConfig openMetadataConfig, RestAPI restAPI,
                           ServicesRepository servicesRepository, ServicesChangeRepository servicesChangeRepository,
                           ServicesEventRepository servicesEventRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.restAPI = restAPI;
        this.servicesRepository = servicesRepository;
        this.servicesChangeRepository = servicesChangeRepository;
        this.servicesEventRepository = servicesEventRepository;
    }

    /**
     * Service Name / Database Type / Connection Status / Owner(Creator) / Created At / Updated At / Description
     * The number of items depend on config (Default is 5)
     */
    @Override
    public List<ServicesChange> getServiceRecentChange() {
        return servicesChangeRepository.findTopByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getSize(),
                        openMetadataConfig.getPageableConfig().getChange().getPage()));
    }

    @Override
    public List<ServicesChange> getServiceRecentChange(UUID serviceID) {
        return servicesChangeRepository.findTopByServiceIDOrderByUpdatedAtDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getSize(),
                        openMetadataConfig.getPageableConfig().getChange().getPage()));
    }

    /**
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return
     */
    @Override
    public List<ServicesEvent> getServiceEvent() {
        var services = servicesRepository.findTopByOrderByUpdatedAtDesc(openMetadataConfig.getNumberOf().getRecentChange());
        return null;
    }

    @Override
    public List<ServicesEvent> getServiceEvent(UUID serviceID) {
        return null;
    }

    @Override
    public List<ServicesConnect> getServiceConnect() {
        return null;
    }

    @Override
    public List<ServicesConnect> getServiceConnect(UUID serviceID) {
        return null;
    }

    @Override
    public Map<String, String> getServiceEvent(String serviceID) {
        return null;
    }

    @Override
    public void runConnection() {

    }
}
