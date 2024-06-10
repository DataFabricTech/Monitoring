package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services_change")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id", nullable = false)
    private Long changeID;
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Column(name = "connect_response_time")
    private int connectResponseTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    private String description;

    @Builder
    public ServicesChange(UUID serviceID, int connectResponseTime, LocalDateTime createdAt, LocalDateTime updatedAt
            , String description) {
        this.serviceID = serviceID;
        this.connectResponseTime = connectResponseTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }
}
