package com.mobigen.monitoring.model;

import lombok.Getter;

@Getter
public class GenericWrapper<T> {
    private final T object;
    private final Long timestamp;

    public GenericWrapper(T object, Long timestamp) {
        this.object = object;
        this.timestamp = timestamp;
    }
}
