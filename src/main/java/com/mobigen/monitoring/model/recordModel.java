package com.mobigen.monitoring.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class recordModel {
    @Builder
    public record ConnectionAvgResponseTime(UUID serviceID, BigDecimal avgResponseTime) {}

    @Builder
    public record ConnectStatusResponse(Long total, Long connected, Long disConnected) {}

    @Builder
    public record ConnectionResponseTime(UUID serviceID, List<BigDecimal> responseTime) {}
}
