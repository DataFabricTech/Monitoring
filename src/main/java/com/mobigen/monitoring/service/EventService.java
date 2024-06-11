package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.dto.ServicesEvent;
import com.mobigen.monitoring.repository.ServicesChangeRepository;
import com.mobigen.monitoring.repository.ServicesEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    OpenMetadataConfig openMetadataConfig;
    ServicesEventRepository servicesEventRepository;
    public EventService(OpenMetadataConfig openMetadataConfig, ServicesEventRepository servicesEventRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesEventRepository = servicesEventRepository;
    }
    /**
     * Updated At / Event Type / Service Name / Database Type / Owner(Creator) / Description
     * The number of items depend on config (Default is 5)
     *
     * @return
     */
    public List<ServicesEvent> getServiceEvent() {
        return servicesEventRepository.findTopByOrderByEventOccurredAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getEvent().getSize(),
                        openMetadataConfig.getPageableConfig().getEvent().getPage()));
    }

    public List<ServicesEvent> getServiceEvent(UUID serviceID) {
        return servicesEventRepository.findTopByServiceIDOrderByEventOccurredAtAsc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getEvent().getSize(),
                        openMetadataConfig.getPageableConfig().getEvent().getPage()));
    }
}
