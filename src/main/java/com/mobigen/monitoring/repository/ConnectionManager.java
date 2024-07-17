package com.mobigen.monitoring.repository;

import com.mobigen.monitoring.config.ConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Map<ConnectionConfig, Connection> connectionPool = new ConcurrentHashMap<>();

    public static Connection getConnection(ConnectionConfig config) throws SQLException {
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

    private static Connection createConnection(ConnectionConfig config) throws SQLException {
        try {
            switch (config.getDatabaseType()) {
                case MINIO:
                    // todo
                    break;
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
                    throw new IllegalArgumentException("Unsupported database type: " + config.getDatabaseType());
            }
            return DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }
}
