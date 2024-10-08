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
    @Schema(description = "Service의 display Name")
    @Column(name = "service_display_name")
    private String displayName;
    @Schema(description = "Service의 타입", example = "Mysql")
    @Column(name = "service_type", nullable = false)
    private String serviceType;
    @Schema(description = "소유자의 이름", example = "admin")
    @Column(name = "owner_name")
    private String ownerName;
    @Schema(description = "Service가 생성된 날짜")
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    @Schema(description = "Service의 업데이트 된 날짜")
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
    @Schema(description = "Service의 Hard Delete 유무")
    private boolean deleted = false;
    @Schema(description = "Service의 Connection 상태값")
    @Enumerated(EnumType.STRING)
    private ConnectionStatus connectionStatus;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ConnectionDTO> connections = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "service_id")
    private List<ConnectionHistoryDTO> connectionHistories = new ArrayList<>();


    @Builder(toBuilder = true)
    public ServiceDTO(UUID serviceID, String name, String displayName, String serviceType, String ownerName, Long createdAt,
                      Long updatedAt, boolean deleted, ConnectionStatus connectionStatus, List<ConnectionDTO> connections,
                      List<ConnectionHistoryDTO> connectionHistories) {
        this.serviceID = serviceID;
        this.name = name;
        this.displayName = displayName;
        this.serviceType = serviceType;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.connectionStatus = connectionStatus;
        this.connections = connections;
        this.connectionHistories = connectionHistories;
    }
}
