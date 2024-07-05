package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.repository.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Repository
@Slf4j
public class MysqlRepository implements DBRepository {
    private Connection conn;

    @Override
    public void getClient(JsonNode serviceJson) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() throws SQLException {
        log.debug("Mysql itemCount Start");
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

    private void getConnection(JsonNode serviceJson) throws SQLException {
        log.debug("Mysql getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        var connectionConfig = ConnectionConfig.builder()
                .databaseType(ConnectionConfig.fromString(connectionConfigJson.get(TYPE.getName()).asText()))
                .url("jdbc:mysql://" + connectionConfigJson.get(HOST_PORT.getName()).asText())
                .userName(connectionConfigJson.get(DB_USER_NAME.getName()).asText())
                .password(connectionConfigJson.get(AUTH_TYPE.getName()).get(PASSWORD.getName()).asText())
                .build();

        try {
            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (SQLException e) {
            log.error("Connection fail: " + e + " Service Name :" + serviceJson.get(NAME.getName()).asText());
            throw e;
        } catch (Exception e) {
            log.error("what is error : " + e);
        }
    }
}
