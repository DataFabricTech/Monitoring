package com.mobigen.monitoring.model.dto.compositeKeys;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
public class ConnectionKey implements Serializable {
    private Long executeAt;
    private String executeBy;

    public ConnectionKey() {}

    @Builder
    public ConnectionKey(Long executeAt, String executeBy) {
        this.executeAt = executeAt;
        this.executeBy = executeBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ConnectionKey) o;
        return Objects.equals(that.executeAt, this.executeAt) &&
                        this.executeBy.equals(that.executeBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.executeAt, this.executeBy);
    }
}
