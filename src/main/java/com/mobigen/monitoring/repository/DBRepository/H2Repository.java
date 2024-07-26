package com.mobigen.monitoring.repository.DBRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.exception.ErrorCode;
import com.mobigen.monitoring.model.enums.DBType;
import com.mobigen.monitoring.repository.ConnectionManager;
import com.mobigen.monitoring.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.CONNECTION;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.NAME;

@Slf4j
public class H2Repository implements DBRepository {
    private Connection conn;
    private final Utils utils = new Utils();

    public H2Repository(JsonNode serviceJson) throws SQLException {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() {
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
                    .message(DBType.POSTGRES.getName())
                    .build();
        }
    }

    @Override
    public void close() throws Exception {
        if (this.conn != null)
            conn.close();
    }

    @Override
    public Long measureExecuteResponseTime() {
        log.debug("Measure postgresql execute query response time");
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
                    .message(DBType.POSTGRES.getName())
                    .build();
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    private void getConnection(JsonNode serviceJson) throws SQLException {
        log.debug("Postgresql getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        try {
            var connectionConfigBuilder = ConnectionConfig.builder()
                    .databaseType(ConnectionConfig.fromString(utils.getAsTextOrNull(connectionConfigJson.get(TYPE.getName()))))
                    .url("jdbc:h2:" + utils.getAsTextOrNull(connectionConfigJson.get(HOST_PORT.getName())))
                    .userName(utils.getAsTextOrNull(connectionConfigJson.get(DB_USER_NAME.getName())));

            var connectionConfig = connectionConfigJson.get(AUTH_TYPE.getName()) == null ?
                    connectionConfigBuilder.password(null).build() :
                    connectionConfigBuilder.password(utils.getAsTextOrNull(
                            connectionConfigJson.get(AUTH_TYPE.getName()).get(PASSWORD.getName()))).build();

            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (ClassNotFoundException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.CONNECTION_FAIL)
                    .message(utils.getAsTextOrNull(serviceJson.get(NAME.getName())))
                    .build();
        }

    }
}
