package com.mobigen.monitoring.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ServicesKey implements Serializable {
    private UUID serviceID;
    private long updatedAt;

    @Builder
    public ServicesKey(UUID serviceID, long updatedAt) {
        this.serviceID = serviceID;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ServicesKey) o;
        return Objects.equals(that.serviceID, this.serviceID) &&
                that.updatedAt == this.updatedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.serviceID, this.updatedAt);
    }
}
