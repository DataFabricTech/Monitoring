package com.mobigen.monitoring.service;

import com.mobigen.monitoring.model.dto.MetadataDTO;
import com.mobigen.monitoring.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetadataService {
    private final MetadataRepository metadataRepository;

    public void saveMetadata(Map<String, String> metadataList) {
        for (var key : metadataList.keySet()) {
            metadataRepository.save(MetadataDTO.builder()
                    .metadataName(key)
                    .metadataValue(metadataList.get(key))
                    .build());
        }
    }

    public Long getRecentCollectedTime() {
        return Long.parseLong(metadataRepository.getRecentCollectedTime());
    }

    public void deleteAll() {
        metadataRepository.deleteAll();
    }
}
