package com.mobigen.monitoring.model.dto.compositeKeys;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class SummarizeHistoryKey {
    private UUID serviceID;
    private String event;

    @Builder
    public SummarizeHistoryKey(UUID serviceID, String event) {
        this.serviceID = serviceID;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (SummarizeHistoryKey) o;
        return Objects.equals(that.serviceID, this.serviceID) &&
                this.event.equals(that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.serviceID, this.event);
    }
}
