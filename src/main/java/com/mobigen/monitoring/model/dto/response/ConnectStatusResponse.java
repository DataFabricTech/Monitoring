package com.mobigen.monitoring.model.dto.response;

import lombok.Builder;

@Builder
public record ConnectStatusResponse(Long total, Long connected, Long disconnected, Long connectError) {
}
