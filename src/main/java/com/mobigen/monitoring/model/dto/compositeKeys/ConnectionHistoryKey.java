package com.mobigen.monitoring.model.dto.compositeKeys;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ConnectionHistoryKey implements Serializable {
    private Long updatedAt;
    private UUID serviceID;

    public ConnectionHistoryKey() {}

    @Builder
    public ConnectionHistoryKey(Long updatedAt, UUID serviceID) {
        this.updatedAt = updatedAt;
        this.serviceID = serviceID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ConnectionHistoryKey) o;
        return Objects.equals(that.updatedAt, this.updatedAt) &&
                this.serviceID.equals(that.serviceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.updatedAt, this.serviceID);
    }
}
