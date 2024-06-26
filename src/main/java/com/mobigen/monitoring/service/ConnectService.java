package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.model.dto.ConnectionConfig;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mobigen.monitoring.model.enums.DBConfig.TYPE;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_FAIL;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_SUCCESS;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
@Slf4j
public class ConnectService {
    final SchedulerConfig schedulerConfig;
    final ServicesConnectRepository servicesConnectRepository;
    final ServicesRepository servicesRepository;
    final ServicesHistoryRepository servicesHistoryRepository;

    final MariadbRepository mariadbRepository;


    public ConnectService(SchedulerConfig schedulerConfig, ServicesConnectRepository servicesConnectRepository,
                          ServicesRepository servicesRepository, ServicesHistoryRepository servicesHistoryRepository,
                          MariadbRepository mariadbRepository) {
        this.schedulerConfig = schedulerConfig;
        this.servicesConnectRepository = servicesConnectRepository;
        this.servicesRepository = servicesRepository;
        this.servicesHistoryRepository = servicesHistoryRepository;
        this.mariadbRepository = mariadbRepository;
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

    private DBRepository getDBRepository(ConnectionConfig.DatabaseType databaseType) {
        return switch (databaseType) {
            case MARIADB -> mariadbRepository;
            case MYSQL -> null;
            case POSTGRES -> null;
            case ORACLE -> null;
            case MINIO -> null;
        };
    }

    @Async
    public void getDBItems(JsonNode serviceJson) {
        var serviceId = UUID.fromString(serviceJson.get(ID.getName()).asText());
        var config = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        var startTimestamp = LocalDateTime.now();
        boolean connectionStatus = true;
        try (DBRepository dbRepository = getDBRepository(ConnectionConfig.fromString(config.get(TYPE.getName()).asText()));
        ) {
            dbRepository.getClient(config);

            // getResponseTime Logic
            var connect = ServicesConnect.builder()
                    .serviceID(serviceId)
                    .startTimestamp(startTimestamp)
                    .endTimestamp(LocalDateTime.now())
                    .build();

            servicesConnectRepository.save(connect);

            // get Database Items(Table or File)
            dbRepository.itemsCount();
        } catch (SQLException e) {
            connectionStatus = false;
        } catch (Exception e) {
            log.error("UnKnown Error");
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
