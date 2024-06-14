package com.mobigen.monitoring.model.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services_connect")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesConnect {
    @Id
    @Column(name = "entity_id", nullable = false)
    private UUID entityID;
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    @Column(name = "service_name", nullable = false, unique = true)
    private String serviceName;
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;

    @Builder(toBuilder = true)
    public ServicesConnect(UUID entityID, UUID serviceID, String serviceName, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        this.entityID = entityID;
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }
}
