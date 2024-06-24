package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.ServicesConnectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConnectService {
    OpenMetadataConfig openMetadataConfig;
    ServicesConnectRepository servicesConnectRepository;
    OpenMetadataService openMetadataService;

    public ConnectService(OpenMetadataConfig openMetadataConfig, ServicesConnectRepository servicesConnectRepository, OpenMetadataService openMetadataService) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesConnectRepository = servicesConnectRepository;
        this.openMetadataService = openMetadataService;
    }

    public List<recordModel.ConnectionAvgResponseTime> getServiceConnectResponseTimeList(int page, int size) {
        var avgResponses = servicesConnectRepository.findServiceIdAndAverageConnectionResponseTime(
                PageRequest.of(page, size));
        List<recordModel.ConnectionAvgResponseTime> responseRecords = new ArrayList<>();
        for (var avgResponse : avgResponses) {
            responseRecords.add(
                    recordModel.ConnectionAvgResponseTime.builder()
                            .serviceID((UUID) avgResponse[0])
                            .avgResponseTime((BigDecimal) avgResponse[1])
                            .build()
            );
        }

        return responseRecords;
    }

    public List<ServicesConnect> getServiceConnectResponseTime(UUID serviceID, int page, int size) {
        return servicesConnectRepository.findByServiceIDOrderByEndTimestampDesc(serviceID,
                PageRequest.of(page, size)
        );
    }

    public ServicesConnect getServicesConnect(UUID entityID) {
        return servicesConnectRepository.findById(entityID).orElse(null);
    }


    public void saveConnect(ServicesConnect entity) {
        servicesConnectRepository.save(entity);
    }

    public void runConnection() {
        // http://192.168.106.104:8585/api/v1/services/databaseServices
        // http://192.168.106.104:8585/api/v1/services/storageServices
    }
}
