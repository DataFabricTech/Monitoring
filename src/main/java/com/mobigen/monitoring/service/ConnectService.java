package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.ServiceDTO;
import com.mobigen.monitoring.model.dto.HistoryDTO;
import com.mobigen.monitoring.model.dto.ConnectDTO;
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
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.ConnectionStatus.*;
import static com.mobigen.monitoring.model.enums.EventType.SERVICE_UPDATED;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectService {
    private final ServicesConnectRepository servicesConnectRepository;
    private final ServicesRepository servicesRepository;
    private final ModelRegistrationRepository modelRegistrationRepository;
    private static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000", "90011"));
    private static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006", "72000", "28P01"));

    private ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue;

    public void setDeque(ConcurrentLinkedDeque<GenericWrapper<ServiceDTO>> servicesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<HistoryDTO>> historiesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ConnectDTO>> connectsQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue) {
        this.servicesQueue = servicesQueue;
        this.historiesQueue = historiesQueue;
        this.connectsQueue = connectsQueue;
        this.modelRegistrationQueue = modelRegistrationQueue;
    }

    public void saveConnects(List<ConnectDTO> connectList) {
        servicesConnectRepository.saveAll(connectList);
    }

    /**
     * ResponseTime을 기준으로 오름차순
     * @param page
     * @param size
     * @return 오름차순 결과값
     */
    public List<ConnectDTO> getServiceConnectResponseTimeAscList(int page, int size) {
        return servicesConnectRepository.findByOrderByQueryExecutionTimeAsc(PageRequest.of(page, size));
    }

    /**
     * ResponseTime을 기준으로 내림차순
     * @param page
     * @param size
     * @return 오름차순 결과값
     */
    public List<ConnectDTO> getServiceConnectResponseTimeDescList(int page, int size) {
        return servicesConnectRepository.findByOrderByQueryExecutionTimeDesc(PageRequest.of(page, size));
    }

    /**
     * serviceID를 갖고 있는 것의 responseTime들
     * @param serviceID
     * @param page
     * @param size
     * @return serviceID의 responseTimes
     */
    public List<ConnectDTO> getServiceConnectResponseTime(UUID serviceID, int page, int size) {
        return servicesConnectRepository.findByServiceIDOrderByExecuteAtDesc(serviceID, PageRequest.of(page, size));
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
        var serviceName = serviceJson.get(NAME.getName()).asText();
        try (DBRepository dbRepository = getDBRepository(serviceJson)) {
            // getResponseTime Logic
            var connect = ConnectDTO.builder()
                    .executeAt(LocalDateTime.now())
                    .executeBy(executorName)
                    .queryExecutionTime(dbRepository.measureExecuteResponseTime())
                    .serviceName(serviceName)
                    .serviceID(serviceId)
                    .build();

            connectsQueue.add(new GenericWrapper<>(connect, LocalDateTime.now()));

            var itemCount = dbRepository.itemsCount();

            // get Database Items(Table or File)
            modelRegistrationRepository.findById(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                    .ifPresentOrElse(service -> {
                                var modelRegistration = service.toBuilder()
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();
                                modelRegistrationQueue.add(new GenericWrapper<>(modelRegistration, LocalDateTime.now()));
                            },
                            () -> {
                                var modelRegistration = ModelRegistration.builder()
                                        .serviceId(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                                        .name(serviceJson.get(NAME.getName()).asText())
                                        .omModelCount(omItemCount)
                                        .modelCount(itemCount)
                                        .build();
                                modelRegistrationQueue.add(new GenericWrapper<>(modelRegistration, LocalDateTime.now()));
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
                var history = HistoryDTO.builder()
                        .serviceID(serviceId)
                        .event(SERVICE_UPDATED)
                        .description(connectionStatus.getName())
                        .updateAt(LocalDateTime.now())
                        .build();
                service = service.toBuilder()
                        .serviceID(UUID.fromString(serviceJson.get(ID.getName()).asText()))
                        .connectionStatus(connectionStatus)
                        .build();

                historiesQueue.add(new GenericWrapper<>(history, LocalDateTime.now()));
                servicesQueue.add(new GenericWrapper<>(service, LocalDateTime.now()));
            }
        }
    }
}
