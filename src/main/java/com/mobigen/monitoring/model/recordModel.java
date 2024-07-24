package com.mobigen.monitoring.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class recordModel {
    @Builder
    public record ConnectStatusResponse(Long total, Long connected, Long disconnected, Long connectError) {}
}
