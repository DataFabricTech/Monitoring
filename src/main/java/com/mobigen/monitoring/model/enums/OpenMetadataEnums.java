package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum OpenMetadataEnums {
    // todo Enum 분리 필요
    // eventType
    ENTITY("entity"),
    ENTITY_TYPE("entityType"),
    ID("id"),
    ENTITY_ID("entityId"),


    CHANGE_DESCRIPTION("changeDescription"),
    EVENT_TYPE("eventType"),
    REQUEST("request"),
    SERVICE("service"),
    SERVICE_NAME("serviceName"),
    SERVICE_TYPE("serviceType"),
    USER_NAME("userName"),
    OWNER("owner"),
    NAME("name"),
    DATABASE_SERVICE("databaseService"),
    STORAGE_SERVICE("storageService"),
    TIMESTAMP("timestamp"),
    UPDATED_AT("updatedAt"),
    UPDATED_BY("updatedBy"),
    WORKFLOW("workflow"),
    UNKNOWN("unKnown"),
    RESPONSE("response"),
    STATUS("status"),
    FIELDS_ADDED("fieldsAdded"),
    FULLY_QUALIFIED_NAME("fullyQualifiedName"),

    ACCESS_TOKEN("accessToken"),
    TOKEN_TYPE("tokenType"),
    BOT_USER("botUser"),
    CONNECTION("connection"),

    CONFIG("config"),
    JWT_TOKEN("JWTToken"),
    ;

    private final String name;

    OpenMetadataEnums(String name) {
        this.name = name;
    }

}
