package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum EventType {
    SERVICE_CREATE("serviceCreated"),
    SERVICE_DELETED("serviceDeleted"),
    SERVICE_UPDATED("serviceUpdated"),

    CONNECTION_CHECK("connectionCheck"),
    DISCONNECTED("disconnected"),
    CONNECTED("connected"),
    CONNECTION_ERROR("error"),

    UNKNOWN("unKnown")
    ;

    private final String name;
    EventType(String name) {this.name = name;}
}
