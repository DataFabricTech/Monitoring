package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services_connect")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ServicesKey.class)
public class serviceConnect {
    @Id
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "connection_status")
    private boolean connectionStatus;
    @Column(name = "connect_response_time")
    private int connectResponseTime;
}
