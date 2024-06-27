package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.model.dto.ConnectionConfig;
import com.mobigen.monitoring.repository.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;

import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.DBConfig.PASSWORD;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.NAME;

@Repository
@Slf4j
public class OracleRepository implements DBRepository {
    private Connection conn;

    @Override
    public void getClient(JsonNode connectionConfigJson) throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        getConnection(connectionConfigJson);
    }

    @Override
    public int itemsCount() throws SQLException {
        log.debug("itemsCount Start");
        var sql = "SELECT COUNT(*) AS Count FROM information_schema.TABLES;";
        try (
                var stmt = conn.createStatement();
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

    private void getConnection(JsonNode connectionConfigJson) throws SQLException {
        log.debug("Oracle getConnection Start");
        var connectionConfig = ConnectionConfig.builder()
                .databaseType(ConnectionConfig.fromString(connectionConfigJson.get(TYPE.getName()).asText()))
                .url("jdbc:mariadb://" + connectionConfigJson.get(HOST_PORT.getName()).asText())
                .userName(connectionConfigJson.get(USER_NAME.getName()).asText())
                .password(connectionConfigJson.get(PASSWORD.getName()).asText())
                .build();

        try {
            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (SQLException e) {
            log.error("Connection fail: " + e + " Service Name :" + connectionConfigJson.get(NAME.getName()));
            throw e;
        }
    }
}
