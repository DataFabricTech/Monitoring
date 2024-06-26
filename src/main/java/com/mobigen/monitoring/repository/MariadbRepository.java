package com.mobigen.monitoring.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;

import static com.mobigen.monitoring.model.enums.DBConfig.*;

@Repository
@Slf4j
public class MariadbRepository implements DBRepository {
    private Connection conn;

    public MariadbRepository() {
    }

    @Override
    public void getClient(JsonNode connectionConfigJson) throws SQLException {
        this.conn = getConnection(connectionConfigJson);
    }

    @Override
    public long itemsCount() {
        long tablesCount = 0;
        try {
            var stmt = conn.createStatement();
            var sql = "SELECT TABLE_SCHEMA AS database_name, " +
                    "COUNT(*) AS table_count FROM information_schema.TABLES " +
                    "WHERE TABLE_TYPE = 'BASE TABLE, VIEW' " +
                    "GROUP BY TABLE_SCHEMA;";
            // TODO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    @Override
    public void close() throws Exception {
        if (this.conn != null)
            conn.close();
    }

    private Connection getConnection(JsonNode connectionConfigJson) throws SQLException {
        /**
         * todo
         * 1. connectionConfigJson -> connectionConfig -> Util로 변환? 곂치는 거 많을거 같은데?
         * 2. getConnection
         * 3. check valid
         *      ex) connection.isValid(config.timeout)
         */
        var connectionConfig = ConnectionConfig.builder() // null check 필요?
                .databaseType(ConnectionConfig.fromString(connectionConfigJson.get(TYPE.getName()).asText()))
                .url(connectionConfigJson.get(HOST_PORT.getName()).asText())
                .userName(connectionConfigJson.get(USER_NAME.getName()).asText())
                .password(connectionConfigJson.get(PASSWORD.getName()).asText())
                .build();

        try {
            this.conn = ConnectionManager.getConnection(connectionConfig);
            return this.conn;
        } catch (SQLException e) {
            log.error("Database Connection fail");
            throw e;
        }
    }
}
