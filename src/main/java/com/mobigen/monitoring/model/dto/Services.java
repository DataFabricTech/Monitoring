package com.mobigen.monitoring.model.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Services {
    @Id
    @Column(name = "entity_id", nullable = false)
    private UUID entityID;
    @Column(name = "service_name", nullable = false)
    private String name;
    @Column(name = "database_type", nullable = false)
    private String databaseType;
    @Column(name = "owner_name")
    private String ownerName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    private boolean deleted = false;
    private boolean connectionStatus = false;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ServicesConnect> connects = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ServicesHistory> histories = new ArrayList<>();


    @Builder(toBuilder = true)
    public Services(UUID entityID, String name, String databaseType, String ownerName, LocalDateTime createdAt,
                    boolean deleted, boolean connectionStatus, List<ServicesConnect> connects,
                    List<ServicesHistory> histories) {
        this.entityID = entityID;
        this.name = name;
        this.databaseType = databaseType;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.deleted = deleted;
        this.connectionStatus = connectionStatus;
        this.connects = connects;
        this.histories = histories;
    }
}
