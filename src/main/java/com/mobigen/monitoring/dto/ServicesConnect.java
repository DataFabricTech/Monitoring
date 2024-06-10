package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "services_connect")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesConnect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connect_id", nullable = false)
    private Long connect_id;
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    @Column(name = "connect_response_time")
    private int connectResponseTime;

    @Builder
    public ServicesConnect(UUID serviceID, int connectResponseTime) {
        this.serviceID = serviceID;
        this.connectResponseTime = connectResponseTime;
    }
}
