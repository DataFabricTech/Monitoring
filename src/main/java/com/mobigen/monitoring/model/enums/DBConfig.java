package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum DBConfig {
    TYPE("type"),
    SCHEMA("scheam"),
    USER_NAME("username"),
    PASSWORD("password"),
    HOST_PORT("hostPort"),
    DATABASE_NAME("databaseName"),
    DATABASE_SCHEMA("databaseSchema"),
    ;
    private final String name;

    DBConfig(String name) {
        this.name = name;
    }
}
