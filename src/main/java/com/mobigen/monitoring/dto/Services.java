package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ServicesKey.class)
public class Services {
    @Id
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "database_type", nullable = false)
    private DatabaseType databaseType;
    private String owner; // TODO Owner UUID?

    private String description;

    @Builder
    public Services(UUID serviceID, String name, DatabaseType databaseType, String owner, boolean connectionStatus,
                    int connectResposneTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.serviceID = serviceID;
        this.name = name;
        this.databaseType = databaseType;
        this.owner = owner;
        this.connectionStatus = connectionStatus;
        this.connectResponseTime = connectResposneTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
