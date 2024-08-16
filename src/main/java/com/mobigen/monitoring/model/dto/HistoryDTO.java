package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.ServicesHistoryKey;
import com.mobigen.monitoring.model.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ServicesHistoryKey.class)
public class HistoryDTO {
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "이벤트가 발생한 시간")
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;
    @Id
    @Schema(description = "이벤트 명")
    @Enumerated(EnumType.STRING)
    private EventType event;
    @Column(name = "description")
    private String description;
    @Schema(description = "이벤트가 발생한 서비스의 UUID")
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Builder(toBuilder = true)
    public HistoryDTO(UUID serviceID, EventType event,String description, LocalDateTime updateAt) {
        this.serviceID = serviceID;
        this.event = event;
        this.description = description;
        this.updateAt = updateAt;
    }
}
