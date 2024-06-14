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
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    private String event;
    private String description;

    @Builder(toBuilder = true)
    public ServicesHistory(UUID serviceID, String event, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String description) {
        this.serviceID = serviceID;
        this.event = event;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }
}
