package com.mobigen.monitoring.model.dto;

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
public class ServicesHistory {
    @Id
    @Column(name = "history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyID;

    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    private String event;

    @Builder(toBuilder = true)
    public ServicesHistory(UUID serviceID, String event, LocalDateTime updatedAt) {
        this.serviceID = serviceID;
        this.event = event;
        this.updatedAt = updatedAt;
    }
}
