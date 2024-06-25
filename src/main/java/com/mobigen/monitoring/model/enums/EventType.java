package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum EventType {
    SERVICE_CREATE("serviceCreated"),
    SERVICE_DELETED("serviceDeleted"),

    CONNECTION_FAIL("connectionFail"),
    CONNECTION_SUCCESS("connectionSuccess"),

    UNKNOWN("unKnown")
    ;

    private final String name;
    EventType(String name) {this.name = name;}
}
