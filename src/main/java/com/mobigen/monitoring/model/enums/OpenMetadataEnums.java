package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum OpenMetadataEnums {
    // eventType
    ENTITY("entity"),
    ENTITY_TYPE("entityType"),
    ENTITY_ID("entityId"),
    ENTITY_DELETED("entityDeleted"),

    CHANGE_DESCRIPTION("changeDescription"),
    EVENT_TYPE("eventType"),
    SERVICE_TYPE("serviceType"),
    USER_NAME("userName"),
    WORKFLOW("workflow"),
    UNKNOWN("unKnown")
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
