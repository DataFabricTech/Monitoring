package com.mobigen.monitoring.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetadataDTO {
    @Id
    @Schema(description = "metadata의 key")
    @Column(name = "metadata_name")
    private String metadataName;
    @Schema(description = "metadata의 값")
    @Column(name = "metadata_value")
    private String metadataValue;

    @Builder
    public MetadataDTO(String metadataName, String metadataValue) {
        this.metadataName = metadataName;
        this.metadataValue = metadataValue;
    }
}
