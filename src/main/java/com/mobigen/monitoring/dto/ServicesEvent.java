package com.mobigen.monitoring.dto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "services_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServicesEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventid", nullable = false)
    private Long event_id;
    @Column(name = "service_id", nullable = false)
    private UUID serviceID;

    private String event;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_occurred_at", nullable = false)
    private LocalDateTime eventOccurredAt;
    private String description;


    @Builder
    public ServicesEvent(UUID serviceID, String event, LocalDateTime eventOccurredAt, String description) {
        this.serviceID = serviceID;
        this.event = event;
        this.eventOccurredAt = eventOccurredAt;
        this.description = description;
    }
}
