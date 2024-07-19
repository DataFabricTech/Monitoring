package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.config.ConnectionConfig;
import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.exception.ErrorCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Map<ConnectionConfig, Connection> connectionPool = new ConcurrentHashMap<>();

    public static Connection getConnection(ConnectionConfig config) throws ClassNotFoundException, SQLException {
        if (!connectionPool.containsKey(config) || connectionPool.get(config).isClosed()) {
            synchronized (ConnectionManager.class) {
                if (!connectionPool.containsKey(config) || connectionPool.get(config).isClosed()) {
                    Connection connection = createConnection(config);
                    connectionPool.put(config, connection);
                }
            }
        }
        return connectionPool.get(config);
    }

    private static Connection createConnection(ConnectionConfig config) throws ClassNotFoundException, SQLException {
        switch (config.getDatabaseType()) {
            case POSTGRES:
                Class.forName("org.postgresql.Driver");
                break;
            case MARIADB:
                Class.forName("org.mariadb.jdbc.Driver");
                break;
            case MYSQL:
                Class.forName("com.mysql.cj.jdbc.Driver");
                break;
            case ORACLE:
                Class.forName("oracle.jdbc.driver.OracleDriver");
                break;
            default:
                throw ConnectionException.builder()
                        .errorCode(ErrorCode.UNSUPPORTED_DB_TYPE)
                        .message(config.getDatabaseType().getName())
                        .build();
        }
        return DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
    }
}
