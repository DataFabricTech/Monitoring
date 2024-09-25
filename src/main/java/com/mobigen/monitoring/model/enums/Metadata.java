package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum Metadata {
    RECENT_COLLECTED_TIME("recent_collected_time"),
    ;

    private final String name;

    Metadata(String name) {
        this.name = name;
    }
}
