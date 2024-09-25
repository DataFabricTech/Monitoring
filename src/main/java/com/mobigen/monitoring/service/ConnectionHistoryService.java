package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.model.dto.response.ConnectHistoryResponse;
import com.mobigen.monitoring.repository.ConnectHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;


@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionHistoryService {
    private final ConnectHistoryRepository connectHistoryRepository;

    public List<ConnectHistoryResponse> getConnectionHistoriesResponse(PageRequest pageRequest) {
        return connectHistoryRepository.findConnectHistoryResponse(pageRequest);
    }

    public List<ConnectHistoryResponse> getConnectionHistoriesResponse(UUID serviceID, PageRequest pageRequest) {
        return connectHistoryRepository.findConnectHistoryResponse(serviceID, pageRequest);
    }

    public List<ConnectionHistoryDTO> getConnectionHistories(UUID serviceID, PageRequest pageRequest) {
        var list = new ArrayList<String>();
        list.add(CONNECTED.getName());
        list.add(CONNECT_ERROR.getName());
        list.add(DISCONNECTED.getName());
        return connectHistoryRepository.findByServiceIDOrderByUpdatedAtDesc(serviceID,
                list, pageRequest);
    }

    public void saveConnectionHistory(List<ConnectionHistoryDTO> historyDTOs) {
        connectHistoryRepository.saveAll(historyDTOs);
    }

    public void deleteConnectionHistory(int retentionDays) {
        var cutOffDays = LocalDateTime.now().minusDays(retentionDays).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        connectHistoryRepository.deleteAllByUpdatedAtLessThan(cutOffDays);
    }

}
