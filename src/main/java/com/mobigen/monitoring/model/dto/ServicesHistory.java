package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.ServicesConnectKey;
import com.mobigen.monitoring.model.dto.compositeKeys.ServicesHistoryKey;
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
public class ServicesHistory {
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "이벤트가 발생한 시간")
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;
    @Id
    @Schema(description = "이벤트 명")
    private String event;
    @Schema(description = "이벤트가 발생한 서비스의 UUID")
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Builder(toBuilder = true)
    public ServicesHistory(UUID serviceID, String event, LocalDateTime updateAt) {
        this.serviceID = serviceID;
        this.event = event;
        this.updateAt = updateAt;
    }
}
