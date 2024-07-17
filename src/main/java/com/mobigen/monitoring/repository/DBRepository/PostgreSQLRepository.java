package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
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
public class PostgreSQLRepository implements DBRepository {
    private Connection conn;

    public PostgreSQLRepository(JsonNode serviceJson) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() throws SQLException {
        log.debug("Postgresql itemsCount Start");
        var sql = "SELECT COUNT(*) AS Count FROM information_schema.TABLES;";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql);
        ) {
            rs.next();
            return rs.getInt("Count");
        } catch (SQLException e) {
            log.error("Count Table error");
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        if (this.conn != null)
            conn.close();
    }

    @Override
    public Long measureExecuteResponseTime() throws SQLException {
        log.debug("Measure postgresql execute query response time");
        var start = Instant.now();
        var sql = "SELECT 1;";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql)
        ) {
            rs.next();
        } catch (SQLException e) {
            log.debug("Measure oracle execute query response time error");
            throw e;
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    private void getConnection(JsonNode serviceJson) throws SQLException {
        log.debug("Postgresql getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        var connectionConfig = ConnectionConfig.builder()
                .databaseType(ConnectionConfig.fromString(connectionConfigJson.get(TYPE.getName()).asText()))
                .url("jdbc:postgresql://" + connectionConfigJson.get(HOST_PORT.getName()).asText() + "/")
                .userName(connectionConfigJson.get(DB_USER_NAME.getName()).asText())
                .password(connectionConfigJson.get(AUTH_TYPE.getName()).get(PASSWORD.getName()).asText())
                .build();

        try {
            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (SQLException e) {
            log.error("Connection fail: " + e + " Service Name :" + serviceJson.get(NAME.getName()).asText());
            throw e;
        }
    }
}
