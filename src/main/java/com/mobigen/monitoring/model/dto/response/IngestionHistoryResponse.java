package com.mobigen.monitoring.model.dto.response;

import java.util.UUID;

public record IngestionHistoryResponse(Long eventAt, String ingestionName, String type, String event,
                                       String state, UUID serviceId, String dbType) {
}
