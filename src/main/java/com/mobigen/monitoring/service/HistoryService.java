package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.EventType.DISCONNECTED;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTED;


@Service
@RequiredArgsConstructor
public class HistoryService {
    final OpenMetadataConfig openMetadataConfig;
    final ServicesHistoryRepository servicesHistoryRepository;

    public List<ServicesHistory> getServiceHistories(int size) {
        return servicesHistoryRepository.findAllByOrderByUpdateAtDesc(
                PageRequest.of(openMetadataConfig.getPageableConfig().getHistory().getPage(),
                        size));
    }

    public List<ServicesHistory> getServiceHistories(UUID serviceID, int page, int size) {
        return servicesHistoryRepository.findServicesHistoriesByServiceIDOrderByUpdateAtDesc(serviceID,
                PageRequest.of(page, size));
    }

    public List<ServicesHistory> getServiceConnectionHistories(UUID serviceID, int page, int size) {
        var list = new ArrayList<String>();
        list.add(CONNECTED.getName());
        list.add(DISCONNECTED.getName());
        return servicesHistoryRepository.findByServiceIDAndEventInOrderByUpdateAtDesc(serviceID,
                list, PageRequest.of(page, size));
    }

    public void saveHistory(List<ServicesHistory> historyServiceList) {
        servicesHistoryRepository.saveAll(historyServiceList);
    }
}
