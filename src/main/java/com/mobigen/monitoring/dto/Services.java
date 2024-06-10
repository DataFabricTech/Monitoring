package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Services {
    @Id
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    private String name;
    @Column(name = "database_type", nullable = false)
    private String databaseType;
    @Column(name = "owner_name")
    private String ownerName;
    private boolean connectionStatus = false;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ServicesConnect> Connects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ServicesChange> Changes = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ServicesEvent> Events = new ArrayList<>();


    @Builder
    public Services(UUID serviceID, String name, String databaseType, String ownerName, boolean connectionStatus) {
        this.serviceID = serviceID;
        this.name = name;
        this.databaseType = databaseType;
        this.ownerName = ownerName;
        this.connectionStatus = connectionStatus;
    }
}
