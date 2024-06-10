package com.mobigen.monitoring.controller;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public abstract class EntityController {
    protected final String entityType;
    protected final String authorizer;

    protected EntityController(String entityType, String authorizer) {
        this.entityType = entityType;
        this.authorizer = authorizer;
    }
}
