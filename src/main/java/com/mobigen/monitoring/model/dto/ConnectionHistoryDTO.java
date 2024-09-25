package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.ConnectionHistoryKey;
import com.mobigen.monitoring.model.enums.ConnectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "connection_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ConnectionHistoryKey.class)
public class ConnectionHistoryDTO {
    @Id
    @Schema(description = "이벤트가 발생한 시간")
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
    @Id
    @Schema(description = "이벤트가 발생한 서비스의 UUID")
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;
    @Schema(description = "변경된 상태의 값")
    @Enumerated(EnumType.STRING)
    private ConnectionStatus connectionStatus;

    @Builder(toBuilder = true)
    public ConnectionHistoryDTO(Long updatedAt, UUID serviceID, ConnectionStatus connectionStatus) {
        this.updatedAt = updatedAt;
        this.serviceID = serviceID;
        this.connectionStatus = connectionStatus;
    }
}
