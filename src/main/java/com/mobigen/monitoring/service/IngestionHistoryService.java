package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.IngestionHistoryDTO;
import com.mobigen.monitoring.model.dto.response.IngestionHistoryResponse;
import com.mobigen.monitoring.repository.IngestionHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestionHistoryService {
    private final IngestionHistoryRepository ingestionHistoryRepository;

    public List<IngestionHistoryResponse> getIngestionHistoryResponse(PageRequest pageRequest) {
        return ingestionHistoryRepository.findIngestionHistoryResponse(pageRequest);
    }

    public List<IngestionHistoryDTO> getIngestionHistories(PageRequest pageRequest) {
        return ingestionHistoryRepository.findAll(pageRequest).getContent();
    }

    public Long getCount() {
        return ingestionHistoryRepository.count();
    }

    public Optional<IngestionHistoryDTO> getIngestionHistory(UUID ingestionRunId) {
        return ingestionHistoryRepository.findIngestionHistoryDTOByIngestionRunId(ingestionRunId);
    }

    public void saveIngestionHistories(List<IngestionHistoryDTO> ingestionHistoryDTOList) {
        ingestionHistoryRepository.saveAll(ingestionHistoryDTOList);
    }

    public void deleteIngestionHistories(Long cutOffEventAt) {
        ingestionHistoryRepository.deleteAllByEventAtLessThan(cutOffEventAt);
    }
}
