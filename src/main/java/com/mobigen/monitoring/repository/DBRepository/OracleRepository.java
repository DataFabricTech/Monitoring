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
import java.util.regex.Pattern;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.DBConfig.*;
import static com.mobigen.monitoring.model.enums.DBConfig.PASSWORD;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Slf4j
public class OracleRepository implements DBRepository {
    private Connection conn;
    private final Utils utils = new Utils();

    public OracleRepository(JsonNode serviceJson) throws SQLException {
        getConnection(serviceJson);
    }

    @Override
    public int itemsCount() {
        log.debug("Oracle itemsCount Start");
        var sql = "SELECT COUNT(*) AS Count " +
                "FROM " +
                "(SELECT view_name AS name, owner FROM all_views " +
                "UNION " +
                "SELECT table_name AS name, owner FROM all_tables)";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql);
        ) {
            rs.next();
            return rs.getInt("Count");
        } catch (SQLException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.EXECUTE_FAIL)
                    .message(DBType.ORACLE.getName())
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
        log.debug("Measure oracle execute query response time");
        var start = Instant.now();
        var sql = "SELECT 1";
        try (
                var stmt = this.conn.createStatement();
                var rs = stmt.executeQuery(sql)
        ) {
            rs.next();
        } catch (SQLException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.MEASURE_FAIL)
                    .message(DBType.ORACLE.getName())
                    .build();
        }
        var end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    public void getConnection(JsonNode serviceJson) throws SQLException {
        log.debug("Oracle getConnection Start");
        var connectionConfigJson = serviceJson.get(CONNECTION.getName()).get(CONFIG.getName());
        var oracleConnectionType = connectionConfigJson.get(ORACLE_CONNECTION_TYPE.getName());

        String type = "";
        if (oracleConnectionType.get(DATABASE_SCHEMA.getName()) != null)
            type = oracleConnectionType.get(DATABASE_SCHEMA.getName()).asText();
        else if (oracleConnectionType.get(ORACLE_SERVICE_NAME.getName()) != null)
            type = oracleConnectionType.get(ORACLE_SERVICE_NAME.getName()).asText();
        else {
            String sidRegex = "SID=([a-zA-Z0-9]+)";
            Pattern pattern = Pattern.compile(sidRegex);
            var matcher = pattern.matcher(oracleConnectionType.get(ORACLE_TNS_CONNECTION.getName()).asText());
            if (matcher.find())
                type = matcher.group(1);
        }

        try {
            var connectionConfig = ConnectionConfig.builder()
                    .databaseType(ConnectionConfig.fromString(utils.getAsTextOrNull(connectionConfigJson.get(TYPE.getName()))))
                    .url("jdbc:oracle:thin:@//" + utils.getAsTextOrNull(connectionConfigJson.get(HOST_PORT.getName())) + "/" + type)
                    .userName(utils.getAsTextOrNull(connectionConfigJson.get(DB_USER_NAME.getName())))
                    .password(utils.getAsTextOrNull(connectionConfigJson.get(PASSWORD.getName())))
                    .build();

            this.conn = ConnectionManager.getConnection(connectionConfig);
        } catch (ClassNotFoundException e) {
            throw ConnectionException.builder()
                    .errorCode(ErrorCode.CONNECTION_FAIL)
                    .message(utils.getAsTextOrNull(serviceJson.get(NAME.getName())))
                    .build();
        }
    }
}
