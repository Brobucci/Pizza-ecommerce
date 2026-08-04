package com.pizzeria.backend.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StationSizeLoadId implements Serializable {

    private Long stationId;

    @Enumerated(EnumType.STRING)
    private Size size;

    protected StationSizeLoadId() {
    }

    public StationSizeLoadId(Long stationId, Size size) {
        this.stationId = stationId;
        this.size = size;
    }

    public Long getStationId() { return stationId; }
    public Size getSize() { return size; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationSizeLoadId)) return false;
        StationSizeLoadId that = (StationSizeLoadId) o;
        return Objects.equals(stationId, that.stationId) && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, size);
    }
}
