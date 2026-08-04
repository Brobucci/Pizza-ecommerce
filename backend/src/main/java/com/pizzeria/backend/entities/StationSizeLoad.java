package com.pizzeria.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "station_size_load")
public class StationSizeLoad {

    @EmbeddedId
    private StationSizeLoadId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stationId")
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "load_units", nullable = false, precision = 4, scale = 2)
    private BigDecimal loadUnits;

    protected StationSizeLoad() {
    }

    public StationSizeLoad(Station station, Size size, BigDecimal loadUnits) {
        this.station = station;
        this.id = new StationSizeLoadId(station.getId(), size);
        this.loadUnits = loadUnits;
    }

    public StationSizeLoadId getId() { return id; }
    public Station getStation() { return station; }
    public Size getSize() { return id.getSize(); }
    public BigDecimal getLoadUnits() { return loadUnits; }

    public void setLoadUnits(BigDecimal loadUnits) { this.loadUnits = loadUnits; }
}