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
    @Column(name = "connect_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectID;
    @Column(name = "service_id")
    private UUID serviceID;
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;

    @Builder(toBuilder = true)
    public ServicesConnect(UUID serviceID, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        this.serviceID = serviceID;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }
}
