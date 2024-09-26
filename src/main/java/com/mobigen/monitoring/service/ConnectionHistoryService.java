package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.model.dto.response.ConnectionHistoryResponse;
import com.mobigen.monitoring.repository.ConnectionHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionHistoryService {
    private final ConnectionHistoryRepository connectionHistoryRepository;

    public List<ConnectionHistoryResponse> getConnectionHistoriesResponse(PageRequest pageRequest) {
        return connectionHistoryRepository.findConnectHistoryResponse(pageRequest);
    }

    public List<ConnectionHistoryResponse> getConnectionHistoriesResponse(UUID serviceID, PageRequest pageRequest) {
        return connectionHistoryRepository.findConnectHistoryResponse(serviceID, pageRequest);
    }

    public List<ConnectionHistoryDTO> getConnectionHistories(UUID serviceID, PageRequest pageRequest) {
        return connectionHistoryRepository.findByServiceIDOrderByUpdatedAtDesc(serviceID, pageRequest);
    }

    public void saveConnectionHistory(List<ConnectionHistoryDTO> historyDTOs) {
        connectionHistoryRepository.saveAll(historyDTOs);
    }

    public void deleteConnectionHistory(int retentionDays) {
        var cutOffDays = LocalDateTime.now().minusDays(retentionDays).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        connectionHistoryRepository.deleteAllByUpdatedAtLessThan(cutOffDays);
    }
}
