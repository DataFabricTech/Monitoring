package com.mobigen.monitoring.model.dto;

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
@Table(name = "ingestion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngestionDTO {
    @Id
    @Schema(description = "ingestion의 UUID")
    @Column(name = "ingestion_id", nullable = false)
    private UUID ingestionID;
    @Schema(description = "ingestion의 이름")
    @Column(name = "ingestion_name", nullable = false)
    private String name;
    @Schema(description = "ingestion의 display name")
    @Column(name = "ingestion_display_name", nullable = false)
    private String displayName;
    @Schema(description = "ingestion의 type")
    @Column(name = "type", nullable = false)
    private String type;
    @Schema(description = "Ingestion이 속해 있는 서비스의 FQN")
    @Column(name = "service_fqn")
    private String serviceFQN;
    @Schema(description = "Ingestion이 속해 있는 서비스의 ID")
    @Column(name = "service_id")
    private UUID serviceID;
    @Schema(description = "Ingestion이 등록/수정된 시간")
    @Column(name = "updated_at")
    private Long updatedAt;
    @Schema(description = "Ingestion의 Hard Delete 유무")
    private boolean deleted;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "ingestion_id")
    private List<IngestionHistoryDTO> ingestionHistories = new ArrayList<>();

    @Builder(toBuilder = true)
    private IngestionDTO(UUID ingestionID, String name, String displayName, String type,
                         String serviceFQN, UUID serviceID, Long updatedAt, boolean deleted, List<IngestionHistoryDTO> ingestionHistories) {
        this.ingestionID = ingestionID;
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.serviceFQN = serviceFQN;
        this.serviceID = serviceID;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.ingestionHistories = ingestionHistories;
    }
}
