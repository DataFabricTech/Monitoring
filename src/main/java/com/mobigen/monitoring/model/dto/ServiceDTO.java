package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.enums.ConnectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ServiceDTO {
    @Id
    @Column(name = "service_id", nullable = false)
    @Schema(description = "Service의 UUID")
    private UUID serviceID;
    @Schema(description = "Service의 이름")
    @Column(name = "service_name", nullable = false)
    private String name;
    @Schema(description = "Service의 타입", example = "Mysql")
    @Column(name = "service_type", nullable = false)
    private String serviceType;
    @Schema(description = "소유자의 이름", example = "admin")
    @Column(name = "owner_name")
    private String ownerName;
    @Schema(description = "Service가 생성된 날짜")
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    @Schema(description = "Service의 Hard Delete 유무")
    private boolean deleted = false;
    @Schema(description = "Service의 Connect 상태값")
    @Enumerated(EnumType.STRING)
    private ConnectionStatus connectionStatus;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ConnectDTO> connects = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<HistoryDTO> histories = new ArrayList<>();


    @Builder(toBuilder = true)
    public ServiceDTO(UUID serviceID, String name, String serviceType, String ownerName, Long createdAt,
                      boolean deleted, ConnectionStatus connectionStatus, List<ConnectDTO> connects,
                      List<HistoryDTO> histories) {
        this.serviceID = serviceID;
        this.name = name;
        this.serviceType = serviceType;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.deleted = deleted;
        this.connectionStatus = connectionStatus;
        this.connects = connects;
        this.histories = histories;
    }
}
