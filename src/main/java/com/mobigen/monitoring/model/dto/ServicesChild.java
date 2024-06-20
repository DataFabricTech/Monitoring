package com.mobigen.monitoring.model.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "servicesChild")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesChild {
    @Id
    @Column(name = "entity_id", nullable = false)
    private UUID entityID;
    @Column(name = "entity_name", nullable = false)
    private String entityName;
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "fully_qualified_name", nullable = false)
    private String fullyQualifiedName;
    private boolean deleted = false;

    @Builder(toBuilder = true)
    public ServicesChild(UUID entityID, String entityName, UUID serviceID, String serviceName, String entityType,
                         LocalDateTime createdAt, boolean deleted, String fullyQualifiedName) {
        this.entityID = entityID;
        this.entityName = entityName;
        this.entityType = entityType;
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.createdAt = createdAt;
        this.deleted = deleted;
        this.fullyQualifiedName = fullyQualifiedName;
    }
}