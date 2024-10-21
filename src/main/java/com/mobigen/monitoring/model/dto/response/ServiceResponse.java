package com.mobigen.monitoring.model.dto.response;

import com.mobigen.monitoring.model.enums.ConnectionStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ServiceResponse(UUID serviceId, String serviceName, String serviceDisplayName, String serviceType,
                              String ownerName, Long createAt, boolean deleted, ConnectionStatus connectionStatus) {
}
