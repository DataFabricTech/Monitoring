package com.mobigen.monitoring.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class ConnectionConfig {
    public enum DatabaseType {
        MINIO, POSTGRES, MARIADB, MYSQL, ORACLE
    }

    private DatabaseType databaseType;
    private String url;
    private String userName;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionConfig that = (ConnectionConfig) o;
        return Objects.equals(databaseType, that.databaseType) && url.equals(that.url) &&
                userName.equals(that.userName) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseType, url, userName, password);
    }

    public static DatabaseType fromString(String str) {
        if (str != null)
            return switch (str.toUpperCase().trim()) {
                case "MARIADB" -> DatabaseType.MARIADB;
                case "MYSQL" -> DatabaseType.MYSQL;
                case "POSTGRES" -> DatabaseType.POSTGRES;
                case "ORACLE" -> DatabaseType.ORACLE;
                case "S3" -> DatabaseType.MINIO;
                default -> throw new IllegalArgumentException("Unsupported database type: " + str);
            };
        throw new IllegalArgumentException("Database type string cannot be null");
    }
}
