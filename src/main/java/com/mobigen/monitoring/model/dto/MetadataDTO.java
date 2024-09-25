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
    @Schema(description = "metadata의 이름")
    @Column(name = "name")
    private String name;
    @Schema(description = "metadata의 값")
    @Column(name = "value")
    private String value;

    @Builder
    public MetadataDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
