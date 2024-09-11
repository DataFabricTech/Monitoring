package com.mobigen.monitoring.model;

import lombok.Builder;

public class recordModel {
    @Builder
    public record ConnectStatusResponse(Long total, Long connected, Long disconnected, Long connectError) {}
}
