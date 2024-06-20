package com.mobigen.monitoring.model.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesHistory {
    @Id
    @Column(name = "entity_id", nullable = false)
    private UUID entityID;

    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    private String event;
    @Column(name = "fully_qualified_name", nullable = false)
    private String fullyQualifiedName;

    @Builder(toBuilder = true)
    public ServicesHistory(UUID entityID, UUID serviceID, String event, LocalDateTime updatedAt, String fullyQualifiedName) {
        this.entityID = entityID;
        this.serviceID = serviceID;
        this.event = event;
        this.updatedAt = updatedAt;
        this.fullyQualifiedName = fullyQualifiedName;
    }
}
