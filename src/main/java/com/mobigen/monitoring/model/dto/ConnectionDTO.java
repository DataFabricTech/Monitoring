package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.ConnectionKey;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "connection")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ConnectionKey.class)
public class ConnectionDTO {
    @Id
    @Schema(description = "응답 시간 측정을 위한 실행 시작 시간")
    @Column(name = "execute_at")
    private Long executeAt;
    @Id
    @Schema(description = "응답 시간 측정을 요청한 사용자의 이름", example = "admin")
    @Column(name = "execute_by")
    private String executeBy;
    @Schema(description = "평균 응답 시간")
    @Column(name = "query_execution_time")
    private Long queryExecutionTime;
    @Schema(description = "응답 시간을 측정한 서비스의 UUID")
    @Column(name = "service_id")
    private UUID serviceID;

    @Builder(toBuilder = true)
    public ConnectionDTO(Long executeAt, String executeBy, Long queryExecutionTime, UUID serviceID) {
        this.executeAt = executeAt;
        this.executeBy = executeBy;
        this.queryExecutionTime = queryExecutionTime;
        this.serviceID = serviceID;
    }
}
