package com.mobigen.monitoring.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GenericWrapper<T> {
    private final T object;
    private final LocalDateTime timestamp;

    public GenericWrapper(T object, LocalDateTime timestamp) {
        this.object = object;
        this.timestamp = timestamp;
    }
}
