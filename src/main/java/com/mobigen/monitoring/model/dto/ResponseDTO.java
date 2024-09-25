package com.mobigen.monitoring.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class ResponseDTO<T> {
    @Schema(description = "DTO Object")
    public T data;
    @Schema(description = "total size")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Long totalSize;
    @Schema(description = "recent collected time")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Long recentCollectedTime;
}
