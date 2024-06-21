package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.CONNECTION_FAIL;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.CONNECTION_SUCCESS;

@Service
public class HistoryService {
    OpenMetadataConfig openMetadataConfig;
    ServicesHistoryRepository servicesHistoryRepository;

    public HistoryService(OpenMetadataConfig openMetadataConfig, ServicesHistoryRepository servicesHistoryRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesHistoryRepository = servicesHistoryRepository;
    }

    public List<ServicesHistory> getUpsertHistory() {
        return servicesHistoryRepository.findAllByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getHistory().getPage(),
                        openMetadataConfig.getPageableConfig().getHistory().getSize()));
    }

    public List<ServicesHistory> getUpsertHistory(UUID serviceID) {
        return servicesHistoryRepository.findTopByServiceIDOrderByUpdatedAtDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getHistory().getPage(),
                        openMetadataConfig.getPageableConfig().getHistory().getSize()));
    }

    public List<ServicesHistory> getServiceHistories(int size) {
        return servicesHistoryRepository.findAllByOrderByUpdatedAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getHistory().getPage(),
                        size));
    }

    public List<ServicesHistory> getServiceHistories(UUID serviceID, int page, int size) {
        return servicesHistoryRepository.findServicesHistoriesByServiceIDOrderByUpdatedAtDesc(serviceID,
                PageRequest.of(page, size));
    }

    public void saveServiceHistory(ServicesHistory entity) {
        if (!servicesHistoryRepository.existsServicesHistoryByEventAndFullyQualifiedNameAndServiceID(
                entity.getEvent(), entity.getFullyQualifiedName(), entity.getServiceID()))
            servicesHistoryRepository.save(entity);
    }

    public List<ServicesHistory> getServiceConnectionHistories(UUID serviceID, int page, int size) {
        var list = new ArrayList<String>();
        list.add(CONNECTION_SUCCESS.getName());
        list.add(CONNECTION_FAIL.getName());
        return servicesHistoryRepository.findByServiceIDAndEventInOrderByUpdatedAtDesc(serviceID,
                list, PageRequest.of(page, size));
    }
}
