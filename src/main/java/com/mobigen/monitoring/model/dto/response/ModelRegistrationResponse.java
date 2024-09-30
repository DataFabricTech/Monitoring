package com.mobigen.monitoring.model.dto.response;

import java.util.UUID;

public record ModelRegistrationResponse(UUID serviceId, String serviceName, String serviceDisplayName, Long updatedAt, int omModelCount,
                                        int modelCount) {
}
