package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.ServicesConnectRepository;
import com.mobigen.monitoring.repository.ServicesHistoryRepository;
import com.mobigen.monitoring.repository.ServicesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_FAIL;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_SUCCESS;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
public class ConnectService {
    final ServicesConnectRepository servicesConnectRepository;
    final ServicesRepository servicesRepository;
    final ServicesHistoryRepository servicesHistoryRepository;


    public ConnectService(ServicesConnectRepository servicesConnectRepository, ServicesRepository servicesRepository,
                          ServicesHistoryRepository servicesHistoryRepository) {
        this.servicesConnectRepository = servicesConnectRepository;
        this.servicesRepository = servicesRepository;
        this.servicesHistoryRepository = servicesHistoryRepository;
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

    /**
     * DB 접속과 관련된 Factory or Abstract Factory 패턴이 들어갈 항목
     *
     * @param ConnectionConfig 접속 정보 관련 Config
     * @return connection 성공 - true
     * connection 실패 혹은 Exception 발생 - false
     */
    public boolean connectionFactory(JsonNode ConnectionConfig) {
        return false;
    }

    @Async
    public void runConnection(JsonNode serviceJson) {
        var serviceId = UUID.fromString(serviceJson.get(ID.getName()).asText());
        var config = serviceJson.get(CONNECTION.getName());
        var startTimestamp = LocalDateTime.now();
        var connectionStatus = connectionFactory(config);

        if (connectionStatus) {
            var connect = ServicesConnect.builder()
                    .serviceID(serviceId)
                    .startTimestamp(startTimestamp)
                    .endTimestamp(LocalDateTime.now())
                    .build();

            servicesConnectRepository.save(connect);
        }

        var history = ServicesHistory.builder()
                .serviceID(serviceId)
                .event(connectionStatus ? CONNECTION_SUCCESS.getName() : CONNECTION_FAIL.getName())
                .updatedAt(LocalDateTime.now())
                .build();

        var service = servicesRepository.findServicesByEntityID(serviceId)
                .toBuilder()
                .connectionStatus(connectionStatus)
                .build();

        servicesRepository.save(service);
        servicesHistoryRepository.save(history);
    }
}
