package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.ConnectionHistoryDTO;
import com.mobigen.monitoring.model.dto.ConnectionDTO;
import com.mobigen.monitoring.model.dto.response.ResponseTimeResponse;
import com.mobigen.monitoring.repository.*;
import com.mobigen.monitoring.repository.DBRepository.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionService {
    private final ServicesConnectResponseRepository servicesConnectResponseRepository;
    private final ServicesRepository servicesRepository;
    private final ModelRegistrationRepository modelRegistrationRepository;
    private static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000", "90011"));
    private static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006", "72000", "28P01"));

    private ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue;

    public void setDeque(ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ConnectionHistoryDTO>> historiesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ConnectionDTO>> connectsQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue) {
        this.servicesQueue = servicesQueue;
        this.historiesQueue = historiesQueue;
        this.connectsQueue = connectsQueue;
        this.modelRegistrationQueue = modelRegistrationQueue;
    }

    public void saveConnects(List<ConnectionDTO> connectList) {
        servicesConnectResponseRepository.saveAll(connectList);
    }

    /**
     *
     * @param pageRequest
     * @return
     */
    public List<ResponseTimeResponse> getConnectionResponseTime(PageRequest pageRequest) {
        return servicesConnectResponseRepository.findResponseTimeResponse(pageRequest);
    }

    /**
     *
     * @param serviceID
     * @param pageRequest
     * @return
     */
    public List<ResponseTimeResponse> getConnectionResponseTime(UUID serviceID, PageRequest pageRequest) {
        return servicesConnectResponseRepository.findResponseTimeResponse(serviceID, pageRequest);
    }

    public Long getCount() {
        return servicesConnectResponseRepository.count();
    }

    private DBRepository getDBRepository(JsonNode serviceJson)
            throws ConnectionException, SQLException, IOException, MinioException {
        return switch (ConnectionConfig.fromString(
                serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(TYPE.getName()).asText())) {
            case MARIADB -> new MariadbRepository(serviceJson);
            case MYSQL -> new MysqlRepository(serviceJson);
            case POSTGRES -> new PostgreSQLRepository(serviceJson);
            case ORACLE -> new OracleRepository(serviceJson);
            case MINIO -> new MinioRepository(serviceJson);
            case H2 -> new H2Repository(serviceJson);
        };
    }

    /**
     * connectionStatus의 기준(connected, disconnected, error)
     * - https://www.ibm.com/docs/ko/db2/11.5?topic=jsri-sqlstates-issued-by-data-server-driver-jdbc-sqlj
     *
     * @param serviceJson
     * @param omItemCount
     * @param executorName
     */
    @Async
    public void getDBItems(JsonNode serviceJson, int omItemCount, String executorName) {
        var serviceId = UUID.fromString(serviceJson.get(ID.getName()).asText());
        var connectionStatus = DISCONNECTED;
        try (DBRepository dbRepository = getDBRepository(serviceJson)) {
            // getResponseTime Logic
            var connect = ConnectionDTO.builder()
                    .executeAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .executeBy(executorName)
                    .queryExecutionTime(dbRepository.measureExecuteResponseTime())
                    .serviceID(serviceId)
                    .build();

            connectsQueue.add(new GenericWrapper<>(connect,
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

            var itemCount = dbRepository.itemsCount();

            // get Database Items(Table or File)
            modelRegistrationRepository.findById(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                    .ifPresentOrElse(service -> {
                                var modelRegistration = service.toBuilder()
                                        .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();
                                modelRegistrationQueue.add(new GenericWrapper<>(modelRegistration,
                                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                            },
                            () -> {
                                var modelRegistration = ModelRegistration.builder()
                                        .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                        .serviceId(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();
                                modelRegistrationQueue.add(new GenericWrapper<>(modelRegistration,
                                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                            });
            connectionStatus = CONNECTED;
        } catch (IOException e) {
            connectionStatus = DISCONNECTED;
            log.error("[NotFoundDB-Error] serviceName: {}", serviceJson.get(NAME.getName()));
        } catch (MinioException e) {
            connectionStatus = CONNECT_ERROR;
            log.error("[Authentication-Error] serviceName: {}", serviceJson.get(NAME.getName()));
        } catch (SQLException e) {
            if (ConnectionFailCode.contains(e.getSQLState())) {
                connectionStatus = DISCONNECTED;
                log.error("[NotFoundDB-Error] serviceName: {}", serviceJson.get(NAME.getName()));
            } else if (AuthenticationFailCode.contains(e.getSQLState())) {
                connectionStatus = CONNECT_ERROR;
                log.error("[Authentication-Error] serviceName: {}", serviceJson.get(NAME.getName()));
            } else {
                connectionStatus = CONNECT_ERROR;
                log.error("[Unknown-Error] serviceName: {}", serviceJson.get(NAME.getName()));
            }
        } catch (ConnectionException e) {
            log.error("[Connection-Error] serviceName: {}, exception: {}, exception message: {}",
                    serviceJson.get(NAME.getName()), e, e.getMessage());
        } catch (Exception e) {
            connectionStatus = CONNECT_ERROR;
            log.error("[Unknown-Error] serviceName: {}, exception: {}, exception message: {}",
                    serviceJson.get(NAME.getName()), e, e.getMessage());
        } finally {
            var service = servicesRepository.findById(serviceId).orElse(ServiceDTO.builder().build());
            if (service != null && (service.getConnectionStatus() == null) || !Objects.requireNonNull(service).getConnectionStatus().equals(connectionStatus)) {
                var connectionHistory = ConnectionHistoryDTO.builder()
                        .serviceID(serviceId)
                        .updatedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .connectionStatus(connectionStatus)
                        .build();
                service = service.toBuilder()
                        .serviceID(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                        .connectionStatus(connectionStatus)
                        .build();

                historiesQueue.add(new GenericWrapper<>(connectionHistory,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                servicesQueue.add(new GenericWrapper<>(service,
                        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
        }
    }
}
