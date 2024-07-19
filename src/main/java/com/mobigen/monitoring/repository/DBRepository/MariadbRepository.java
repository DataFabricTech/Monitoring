package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.exception.ErrorCode;
import com.mobigen.monitoring.model.enums.DBType;
import com.mobigen.monitoring.repository.ConnectionManager;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Slf4j
public class MariadbRepository implements DBRepository {
    private Connection conn;

    public MariadbRepository(JsonNode serviceJson) {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() {
        log.debug("Mariadb itemsCount Start");
        var sql = "SELECT COUNT(*) AS Count FROM information_schema.TABLES;";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql);
        ) {
            rs.next();
            return rs.getInt("Count");
        } catch (SQLException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.EXECUTE_FAIL)
                    .message(DBType.MARIADB.getName())
                    .build();
        }
    }

    @Override
    public void close() throws Exception {
        if (!this.conn.isClosed())
            conn.close();
    }

    @Override
    public Long measureExecuteResponseTime() {
        log.debug("Measure mariadb execute query response time");
        var start = Instant.now();
        var sql = "SELECT 1;";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql)
        ) {
            rs.next();
        } catch (SQLException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.MEASURE_FAIL)
                    .message(DBType.MARIADB.getName())
                    .build();
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    private void getConnection(JsonNode serviceJson) {
        log.debug("Mariadb getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        try {
            var connectionConfig = ConnectionConfig.builder()
                    .databaseType(ConnectionConfig.fromString(connectionConfigJson.get(TYPE.getName()).asText()))
                    .url("jdbc:mariadb://" + connectionConfigJson.get(HOST_PORT.getName()).asText())
                    .userName(connectionConfigJson.get(DB_USER_NAME.getName()).asText())
                    .password(connectionConfigJson.get(PASSWORD.getName()).asText())
                    .build();

            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (SQLException e) {
            System.out.println("!~");
        } catch (ClassNotFoundException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.CONNECTION_FAIL)
                    .message(serviceJson.get(NAME.getName()).asText())
                    .build();
        }
    }
}
