package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.IngestionDTO;
import com.mobigen.monitoring.repository.IngestionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class IngestionsService {
    private final IngestionsRepository ingestionsRepository;

    public List<IngestionDTO> getIngestionList() {
        return ingestionsRepository.findAll();
    }

    public Optional<IngestionDTO> getIngestion(UUID ingestionID) {
        return ingestionsRepository.findById(ingestionID);
    }

    public void saveIngestions(List<IngestionDTO> ingestionDTOList) {
        ingestionsRepository.saveAll(ingestionDTOList);
    }

    public void deleteAll() {
        ingestionsRepository.deleteAll();
    }
}
