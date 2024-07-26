package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.PageableConfig;
import com.mobigen.monitoring.model.dto.HistoryDTO;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;


@Service
@RequiredArgsConstructor
public class HistoryService {
    private final PageableConfig pageableConfig;
    private final ServicesHistoryRepository servicesHistoryRepository;

    public List<HistoryDTO> getServiceHistories(int size) {
        return servicesHistoryRepository.findAllByOrderByUpdateAtDesc(
                PageRequest.of(pageableConfig.getPageableConfig().getHistory().getPage(),
                        size));
    }

    public List<HistoryDTO> getServiceHistories(UUID serviceID, int page, int size) {
        return servicesHistoryRepository.findServicesHistoriesByServiceIDOrderByUpdateAtDesc(serviceID,
                PageRequest.of(page, size));
    }

    public List<HistoryDTO> getServiceConnectionHistories(UUID serviceID, int page, int size) {
        var list = new ArrayList<String>();
        list.add(CONNECTED.getName());
        list.add(CONNECT_ERROR.getName());
        list.add(DISCONNECTED.getName());
        return servicesHistoryRepository.findByServiceIDAndDescriptionInOrderByUpdateAtDesc(serviceID,
                list, PageRequest.of(page, size));
    }

    public void saveHistory(List<HistoryDTO> historyDTOs) {
        servicesHistoryRepository.saveAll(historyDTOs);
    }
}
