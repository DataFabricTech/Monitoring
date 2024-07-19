package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.model.GenericWrapper;
import com.mobigen.monitoring.model.dto.ModelRegistration;
import com.mobigen.monitoring.model.dto.Services;
import com.mobigen.monitoring.model.dto.ServicesHistory;
import com.mobigen.monitoring.model.dto.ServicesConnect;
import com.mobigen.monitoring.model.enums.DBType;
import com.mobigen.monitoring.repository.*;
import com.mobigen.monitoring.repository.DBRepository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_FAIL;
import static com.mobigen.monitoring.model.enums.EventType.CONNECTION_SUCCESS;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectService {
    final ServicesConnectRepository servicesConnectRepository;
    final ServicesRepository servicesRepository;
    final ServicesHistoryRepository servicesHistoryRepository;
    final ModelRegistrationRepository modelRegistrationRepository;

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

    private DBRepository getDBRepository(DBType databaseType, JsonNode serviceJson)
            throws ConnectionException, SQLException {
        return switch (databaseType) {
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
        boolean connectionStatus = false;
        var serviceName = serviceJson.get(NAME.getName()).asText();
        try (DBRepository dbRepository = getDBRepository(ConnectionConfig.fromString(
                serviceJson.get(CONNECTION.getName()).get(CONFIG.getName()).get(TYPE.getName()).asText()), serviceJson)
        ) {
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
            connectionStatus = true;
        } catch (SQLException e) {
            switch (e.getSQLState().substring(0, 2)) {
                case "08":
                    break;
                case "28", "42":
                    break;

            }
            /**
             * todo 여기서 histor의 event를 지정해 줘야한다. connected, disconnected, error
             * 08 -> Disconnected
             * 28 -> error
             */

        } catch (Exception e) {
            // todo 여기는 Close error이다.
        } finally {
            var service = servicesRepository.findById(serviceId).orElse(Services.builder().build());
            if (Objects.requireNonNull(service).isConnectionStatus() != connectionStatus) {
                var history = ServicesHistory.builder()
                        .serviceID(serviceId)
                        .event(service.isConnectionStatus() ? CONNECTION_SUCCESS.getName() : CONNECTION_FAIL.getName())
                        .updateAt(LocalDateTime.now())
                        .build();

                service = service.toBuilder()
                        .connectionStatus(connectionStatus)
                        .build();

                historiesQueue.add(new GenericWrapper<>(history, LocalDateTime.now()));
                servicesQueue.add(new GenericWrapper<>(service, LocalDateTime.now()));
            }
        }
    }
}
