package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.repository.ServicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServicesService {
    final ServicesRepository servicesRepository;

    public Long countByConnectionStatusIsTrue() {
        return servicesRepository.countByConnectionStatusIsTrueAndDeletedIsFalse();
    }

    public Long countByConnectionStatusIsFalse() {
        return servicesRepository.countByConnectionStatusIsFalseAndDeletedIsFalse();
    }

    public Long getServicesCount() {
        return servicesRepository.countServicesByDeletedIsFalse();
    }

    public List<Services> getServicesList() {
        return servicesRepository.findAll();
    }

    public Optional<Services> getServices(UUID serviceID) {
        return servicesRepository.findById(serviceID);
    }

    public void saveServices(List<Services> servicesList) {
        servicesRepository.saveAll(servicesList);
    }
}
