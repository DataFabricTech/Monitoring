package com.mobigen.monitoring.config;

import com.mobigen.monitoring.exception.ConnectionException;
import com.mobigen.monitoring.exception.ErrorCode;
import com.mobigen.monitoring.model.enums.DBType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class ConnectionConfig {
    private DBType databaseType;
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

    public static DBType fromString(String str) {
        if (str != null)
            return switch (str.toUpperCase().trim()) {
                case "MARIADB" -> DBType.MARIADB;
                case "MYSQL" -> DBType.MYSQL;
                case "POSTGRES" -> DBType.POSTGRES;
                case "ORACLE" -> DBType.ORACLE;
                case "S3", "MINIO3" -> DBType.MINIO;
                default -> throw ConnectionException.builder()
                                .errorCode(ErrorCode.UNSUPPORTED_DB_TYPE)
                                .message(str)
                                .build();

            };
        throw ConnectionException.builder()
                .errorCode(ErrorCode.UNSUPPORTED_DB_TYPE)
                .message(str)
                .build();
    }
}
