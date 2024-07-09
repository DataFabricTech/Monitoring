package com.mobigen.monitoring.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "model_registration")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelRegistration {
    @Id
    @Schema(description = "Service의 UUID")
    @Column(name = "service_id", nullable = false)
    private UUID serviceId;
    @Schema(description = "Service의 이름")
    @Column(name = "service_name", nullable = false)
    private String name;
    @Schema(description = "OpenMetadata에 등록되어 있는 데이터 모델의 개수", defaultValue = "100")
    @Column(name = "om_model_count", nullable = false)
    private int omModelCount;
    @Schema(description = "실제 DB에 등록되어 있는 Table/View/File의 개수", defaultValue = "100")
    @Column(name = "model_count", nullable = false)
    private int modelCount;

    @Builder(toBuilder = true)
    public ModelRegistration(UUID serviceId, String name, int omModelCount, int modelCount) {
        this.serviceId = serviceId;
        this.name = name;
        this.omModelCount = omModelCount;
        this.modelCount = modelCount;
    }
}
