package com.mobigen.monitoring.model.dto;

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
    @Column(name = "service_id", nullable = false)
    private UUID serviceId;
    @Column(name = "service_name", nullable = false)
    private String name;
    @Column(name = "om_model_count", nullable = false)
    private int omModelCount;
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
