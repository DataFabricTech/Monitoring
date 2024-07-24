package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum DBConfig {
    TYPE("type"),
    DB_USER_NAME("username"),
    AUTH_TYPE("authType"),
    PASSWORD("password"),
    HOST_PORT("hostPort"),
    DATABASE_SCHEMA("databaseSchema"),

    // MINIO
    AWS_CONFIG("awsConfig"),
    AWS_ACCESS_KEY_ID("awsAccessKeyId"),
    AWS_SECRET_ACCESS_KEY("awsSecretAccessKey"),
    END_POINT_URL("endPointURL"),


    ORACLE_CONNECTION_TYPE("oracleConnectionType"),
    ORACLE_SERVICE_NAME("oracleServiceName"),
    ORACLE_TNS_CONNECTION("oracleTNSConnection"),
    ;
    private final String name;

    DBConfig(String name) {
        this.name = name;
    }
}
