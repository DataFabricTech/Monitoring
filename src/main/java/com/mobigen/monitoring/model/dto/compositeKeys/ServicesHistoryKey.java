package com.mobigen.monitoring.model.dto.compositeKeys;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class ServicesHistoryKey implements Serializable {
    private LocalDateTime updateAt;
    private String event;

    public ServicesHistoryKey() {}

    @Builder
    public ServicesHistoryKey(LocalDateTime updateAt, String event) {
        this.updateAt = updateAt;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ServicesHistoryKey) o;
        return Objects.equals(that.updateAt, this.updateAt) &&
                this.event.equals(that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.updateAt, this.event);
    }
}
