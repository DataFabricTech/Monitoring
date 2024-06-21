package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum OpenMetadataEnums {
    // eventType
    ENTITY("entity"),
    ENTITY_TYPE("entityType"),
    ID("id"),
    ENTITY_ID("entityId"),
    ENTITY_CREATE("entityCreated"),
    ENTITY_DELETED("entityDeleted"),

    CONNECTION_FAIL("connectionFail"),
    CONNECTION_SUCCESS("connectionSuccess"),

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
    WORKFLOW("workflow"),
    UNKNOWN("unKnown"),
    RESPONSE("response"),
    STATUS("status"),
    FIELDS_ADDED("fieldsAdded"),
    FULLY_QUALIFIED_NAME("fullyQualifiedName")
    ;

    private final String name;

    OpenMetadataEnums(String name) {
        this.name = name;
    }

    public static OpenMetadataEnums fromString(String name) {
        for (var type: OpenMetadataEnums.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
