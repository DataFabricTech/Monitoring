package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum OpenMetadataEnums {
    ID("id"),
    SERVICE_TYPE("serviceType"),
    DATA("data"),
    NAME("name"),
    UPDATED_AT("updatedAt"),
    UPDATED_BY("updatedBy"),
    ACCESS_TOKEN("accessToken"),
    TOKEN_TYPE("tokenType"),
    BOT_USER("botUser"),
    CONNECTION("connection"),
    JWT_TOKEN("JWTToken"),
    ;

    private final String name;

    OpenMetadataEnums(String name) {
        this.name = name;
    }

}
