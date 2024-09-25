package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.IngestionHistoryKey;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ingestion_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(IngestionHistoryKey.class)
public class IngestionHistoryDTO {
    @Id
    @Schema(description = "이벤트가 발생한 시간" +
            "기준: 등록(Add), 수정(Edit) - UpdatedAt" +
            "     삭제(Delete) - MonitoringTime" +
            "     현황 변경(State Change) - End Date")
    @Column(name = "event_at")
    private Long eventAt;
    @Id
    @Schema(description = "ingestion의 id")
    @Column(name = "ingestion_id")
    private UUID ingestionID;
    @Schema(description = "ingestion의 run id")
    @Column(name = "run_id")
    private UUID ingestionRunId;
    @Schema(description = "발생한 Event" +
            "Event 종류 예시: Add, Edit, Delete, Status Change")
    @Column(name = "event")
    private String event;
    @Schema(description = "Ingestion의 상태")
    @Column(name = "state")
    private String state;

    @Builder(toBuilder = true)
    public IngestionHistoryDTO(Long eventAt, UUID ingestionID, UUID ingestionRunId, String event, String state) {
        this.eventAt = eventAt;
        this.ingestionID = ingestionID;
        this.ingestionRunId = ingestionRunId;
        this.state = state;
        this.event = event;
    }
}
