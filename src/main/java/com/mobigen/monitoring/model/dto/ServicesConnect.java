package com.mobigen.monitoring.model.dto;

import com.mobigen.monitoring.model.dto.compositeKeys.ServicesConnectKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services_connect")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ServicesConnectKey.class)
public class ServicesConnect {
    @Id
    @Column(name = "execute_at")
    private LocalDateTime executeAt;
    @Id
    @Column(name = "execute_by")
    private String executeBy;
    @Column(name = "query_execution_time")
    private Long queryExecutionTime;
    @Column(name = "service_name")
    private String serviceName;
    @Column(name = "service_id")
    private UUID serviceID;

    @Builder(toBuilder = true)
    public ServicesConnect(LocalDateTime executeAt, String executeBy, Long queryExecutionTime, String serviceName, UUID serviceID) {
        this.executeAt = executeAt;
        this.executeBy = executeBy;
        this.queryExecutionTime = queryExecutionTime;
        this.serviceID = serviceID;
        this.serviceName = serviceName;
    }
}
