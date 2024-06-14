package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import org.json.simple.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistoryService {
    OpenMetadataConfig openMetadataConfig;
    ServicesHistoryRepository servicesHistoryRepository;
    public HistoryService(OpenMetadataConfig openMetadataConfig, ServicesHistoryRepository servicesHistoryRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesHistoryRepository = servicesHistoryRepository;
    }

    public List<ServicesHistory> getUpsertHistory() {
        return servicesHistoryRepository.findTopByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getPage(),
                        openMetadataConfig.getPageableConfig().getChange().getSize()));
    }

    public List<ServicesHistory> getUpsertHistory(UUID serviceID) {
        return servicesHistoryRepository.findTopByServiceIDOrderByUpdatedAtDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getPage(),
                        openMetadataConfig.getPageableConfig().getChange().getSize()));
    }

    public List<ServicesHistory> getServiceHistory() {
        return servicesHistoryRepository.findTopByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getChange().getPage(),
                        openMetadataConfig.getPageableConfig().getChange().getSize()));
    }

    public List<ServicesHistory> getServiceHistory(UUID serviceID) {
        return servicesHistoryRepository.findTopByServiceIDOrderByUpdatedAtDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getEvent().getPage(),
                        openMetadataConfig.getPageableConfig().getEvent().getSize()));
    }

    public void saveServiceCreate(ServicesHistory entity) { // todo exception 처리 필요
        servicesHistoryRepository.save(entity);
    }
}
