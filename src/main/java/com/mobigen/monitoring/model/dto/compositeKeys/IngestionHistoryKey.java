package com.mobigen.monitoring.model.dto.compositeKeys;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class IngestionHistoryKey implements Serializable {
    private Long eventAt;
    private UUID ingestionID;

    public IngestionHistoryKey() {}

    @Builder
    public IngestionHistoryKey(Long eventAt, UUID ingestionID) {
        this.eventAt = eventAt;
        this.ingestionID = ingestionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (IngestionHistoryKey) o;
        return Objects.equals(that.eventAt, this.eventAt) &&
                this.ingestionID.equals(that.ingestionID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.eventAt, this.ingestionID);
    }
}
