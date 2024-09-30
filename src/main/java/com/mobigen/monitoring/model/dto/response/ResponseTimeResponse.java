package com.mobigen.monitoring.model.dto.response;


import java.util.UUID;

public record ResponseTimeResponse(UUID serviceId, String serviceName, String serviceDisplayName, Long executeAt, String executeBy,
                                   Long queryExecutionTime) {
}

