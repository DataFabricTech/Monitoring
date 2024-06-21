package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.model.ResponseRecord;
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
    public ConnectService(OpenMetadataConfig openMetadataConfig, ServicesConnectRepository servicesConnectRepository) {
        this.openMetadataConfig = openMetadataConfig;
        this.servicesConnectRepository = servicesConnectRepository;
    }

    public List<ResponseRecord.ConnectionAvgResponse> getServiceConnectList(int page, int size) {
        var avgResponses =servicesConnectRepository.findServiceIdAndAverageConnectionResponseTime(
                PageRequest.of(page, size));
        List<ResponseRecord.ConnectionAvgResponse> responseRecords = new ArrayList<>();
        for (var avgResponse: avgResponses) {
            responseRecords.add(
                    ResponseRecord.ConnectionAvgResponse.builder()
                            .serviceID((UUID) avgResponse[0])
                            .avgResponseTime((BigDecimal) avgResponse[1])
                            .build()
            );
        }

        return responseRecords;
    }

    public List<ServicesConnect> getServiceConnectList(UUID serviceID) {
        return servicesConnectRepository.findTopByOrderByEndTimestampDesc(
                serviceID,
                PageRequest.of(openMetadataConfig.getPageableConfig().getConnect().getPage(),
                        openMetadataConfig.getPageableConfig().getConnect().getSize()));
    }

    public ServicesConnect getServicesConnect(UUID entityID) {
        return servicesConnectRepository.findById(entityID).orElse(null);
    }


    public void saveConnect(ServicesConnect entity) { servicesConnectRepository.save(entity);}

    public void runConnection() {
    }
}
