package com.mobigen.monitoring.model.dto.response;

import com.mobigen.monitoring.model.enums.ConnectionStatus;

import java.util.UUID;

public record ConnectionHistoryResponse(UUID serviceId, String serviceName, String serviceType,
                                        ConnectionStatus connectionStatus) {
}
