package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.repository.*;
import com.mobigen.monitoring.repository.DBRepository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.EventType.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectService {
    final ServicesConnectRepository servicesConnectRepository;
    final ServicesRepository servicesRepository;
    final ServicesHistoryRepository servicesHistoryRepository;
    final ModelRegistrationRepository modelRegistrationRepository;
    static final List<String> ConnectionFailCode = new ArrayList<>(Arrays.asList("08000", "08001", "08S01", "22000"));
    static final List<String> AuthenticationFailCode = new ArrayList<>(Arrays.asList("28000", "08004", "08006"));

    private ConcurrentLinkedDeque<GenericWrapper<Services>> servicesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ServicesHistory>> historiesQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ServicesConnect>> connectsQueue;
    private ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue;

    public void setDeque(ConcurrentLinkedDeque<GenericWrapper<Services>> servicesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ServicesHistory>> historiesQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ServicesConnect>> connectsQueue,
                         ConcurrentLinkedDeque<GenericWrapper<ModelRegistration>> modelRegistrationQueue) {
        this.servicesQueue = servicesQueue;
        this.historiesQueue = historiesQueue;
        this.connectsQueue = connectsQueue;
        this.modelRegistrationQueue = modelRegistrationQueue;
    }

    public void saveConnects(List<ServicesConnect> connectList) {
        servicesConnectRepository.saveAll(connectList);
    }


    public List<ServicesConnect> getServiceConnectResponseTimeAscList(int page, int size) {
        return servicesConnectRepository.findByOrderByQueryExecutionTimeAsc(PageRequest.of(page, size));
    }

    public List<ServicesConnect> getServiceConnectResponseTimeDescList(int page, int size) {
        return servicesConnectRepository.findByOrderByQueryExecutionTimeDesc(PageRequest.of(page, size));
    }

    public List<ServicesConnect> getServiceConnectResponseTime(UUID serviceID, int page, int size) {
        return servicesConnectRepository.findByServiceIDOrderByExecuteAtDesc(serviceID, PageRequest.of(page, size));
    }

    private DBRepository getDBRepository(JsonNode serviceJson)
            throws ConnectionException, SQLException {
        return switch (ConnectionConfig.fromString(
                serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(TYPE.getName()).asText())) {
            case MARIADB -> new MariadbRepository(serviceJson);
            case MYSQL -> new MysqlRepository(serviceJson);
            case POSTGRES -> new PostgreSQLRepository(serviceJson);
            case ORACLE -> new OracleRepository(serviceJson);
            case MINIO -> new MinioRepository(serviceJson);
        };
    }

    /**
     * connectionStatus의 기준(connected, disconnected, error)
     * - https://www.ibm.com/docs/ko/db2/11.5?topic=jsri-sqlstates-issued-by-data-server-driver-jdbc-sqlj
     *  todo 위의 SQLState에 따른 Unit Test 제작 필요
     *
     * @param serviceJson
     * @param omItemCount
     * @param executorName
     */
    @Async
    public void getDBItems(JsonNode serviceJson, int omItemCount, String executorName) {
        var serviceId = UUID.fromString(serviceJson.get(ID.getName()).asText());
        String connectionStatus = DISCONNECTED.getName();
        var serviceName = serviceJson.get(NAME.getName()).asText();
        try (DBRepository dbRepository = getDBRepository(serviceJson)) {
            // getResponseTime Logic
            var connect = ServicesConnect.builder()
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
            connectionStatus = CONNECTED.getName();
        } catch (UnknownHostException e) {
            System.out.println(serviceJson.get(NAME.getName()));
        } catch (SQLException e) {
            if (ConnectionFailCode.contains(e.getSQLState())) {
                connectionStatus = DISCONNECTED.getName();
                log.error("[NotFoundDB-Error] serviceName: {}", serviceJson.get(NAME.getName()));
            } else if (AuthenticationFailCode.contains(e.getSQLState())) {
                connectionStatus = CONNECTION_ERROR.getName();
                log.error("[Authentication-Error] serviceName: {}", serviceJson.get(NAME.getName()));
            } else {
                connectionStatus = CONNECTION_ERROR.getName();
                log.error("[Unknown-Error] serviceName: {}" , serviceJson.get(NAME.getName()));
            }
        } catch (ConnectionException e) {
            log.error("[Connection-Error] serviceName: {}, exception: {}, exception message: {}",
                    serviceJson.get(NAME.getName()), e, e.getMessage());
        } catch (Exception e) {
            connectionStatus = CONNECTION_ERROR.getName();
            log.error("[Unknown-Error] serviceName: {}, exception: {}, exception message: {}",
                    serviceJson.get(NAME.getName()), e, e.getMessage());
        } finally {
            var service = servicesRepository.findById(serviceId).orElse(Services.builder().build());
            if (service != null && (service.getConnectionStatus() == null) || !service.getConnectionStatus().equalsIgnoreCase(connectionStatus)) {
                var history = ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(connectionStatus)
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
