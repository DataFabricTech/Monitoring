package com.mobigen.monitoring.model.enums;

import lombok.Getter;

@Getter
public enum Common {

    CONFIG("config"),
    ;

    private final String name;

    Common(String name) {
        this.name = name;
    }
}
