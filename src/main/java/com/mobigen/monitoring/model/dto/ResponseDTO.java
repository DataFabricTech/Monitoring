package com.mobigen.monitoring.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class ResponseDTO<T> {
    @Schema(description = "DTO Object")
    public T data;
    @Schema(description = "total size")
    public long totalSize;
}
