package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum DBType {
    ORACLE("Oracle"),
    MINIO("Minio"),
    MYSQL("Mysql"),
    MARIADB("Mariadb"),
    POSTGRES("Postgresql")
    ;
    private final String name;
    DBType(String name) {this.name = name;}
}
