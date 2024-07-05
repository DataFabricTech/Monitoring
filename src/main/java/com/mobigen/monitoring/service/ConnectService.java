package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.SchedulerConfig;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.model.recordModel;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.*;
import com.mobigen.monitoring.repository.DBRepository.*;
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

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
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
    final ModelRegistrationRepository modelRegistrationRepository;

    final MariadbRepository mariadbRepository;
    final MysqlRepository mysqlRepository;
    final PostgreSQLRepository postgreSQLRepository;
    final OracleRepository oracleRepository;
    final MinioRepository minioRepository;


    public ConnectService(SchedulerConfig schedulerConfig, ServicesConnectRepository servicesConnectRepository,
                          ServicesRepository servicesRepository, ServicesHistoryRepository servicesHistoryRepository, ModelRegistrationRepository modelRegistrationRepository,
                          MariadbRepository mariadbRepository, MysqlRepository mysqlRepository, PostgreSQLRepository postgreSQLRepository, OracleRepository oracleRepository, MinioRepository minioRepository) {
        this.schedulerConfig = schedulerConfig;
        this.servicesConnectRepository = servicesConnectRepository;
        this.servicesRepository = servicesRepository;
        this.servicesHistoryRepository = servicesHistoryRepository;
        this.modelRegistrationRepository = modelRegistrationRepository;
        this.mariadbRepository = mariadbRepository;
        this.mysqlRepository = mysqlRepository;
        this.postgreSQLRepository = postgreSQLRepository;
        this.oracleRepository = oracleRepository;
        this.minioRepository = minioRepository;
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
            case MARIADB -> this.mariadbRepository;
            case MYSQL -> this.mysqlRepository;
            case POSTGRES -> this.postgreSQLRepository;
            case ORACLE -> this.oracleRepository;
            case MINIO -> this.minioRepository;
        };
    }

    @Async
    public void getDBItems(JsonNode serviceJson, int omItemCount) {
        var serviceId = UUID.fromString(serviceJson.get(ID.getName()).asText());
        var startTimestamp = LocalDateTime.now();
        boolean connectionStatus = false;
        try (DBRepository dbRepository = getDBRepository(ConnectionConfig.fromString(
                serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(TYPE.getName()).asText()));
        ) {
            dbRepository.getClient(serviceJson);

            // getResponseTime Logic
            var connect = ServicesConnect.builder()
                    .serviceID(serviceId)
                    .startTimestamp(startTimestamp)
                    .endTimestamp(LocalDateTime.now())
                    .build();

            servicesConnectRepository.save(connect);
            var itemCount = dbRepository.itemsCount();

            // get Database Items(Table or File)
            modelRegistrationRepository.findById(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                    .ifPresentOrElse(service -> {
                                var modelRegistration = service.toBuilder()
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();
                                modelRegistrationRepository.save(modelRegistration);
                            },
                            () -> {
                                var modelRegistration = ModelRegistration.builder()
                                        .serviceId(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                                        .name(serviceJson.get(NAME.getName()).asText())
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();

                                modelRegistrationRepository.save(modelRegistration);
                            });
            connectionStatus = true;
        } catch (SQLException | IllegalArgumentException e) {
            log.error("Connection fail: " + e + "\nService Name :\t" + serviceJson.get(NAME.getName()).asText());
        } catch (Exception e) {
            log.error("UnKnown Error: " + e + "\nService Name :\t" + serviceJson.get(NAME.getName()).asText());
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
