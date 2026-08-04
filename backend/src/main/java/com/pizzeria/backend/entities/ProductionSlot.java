package com.pizzeria.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	    name = "production_slot",
	    uniqueConstraints = @UniqueConstraint(
	        name = "uq_station_slot",
	        columnNames = {"station_id", "slot_start"}
	    )
	)
public class ProductionSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "slot_start", nullable = false)
    private LocalDateTime slotStart;

    @Column(name = "slot_end", nullable = false)
    private LocalDateTime slotEnd;
	
 // copiato da capacity_profile.capacity_per_slot al momento della generazione:
    // un cambio successivo del profilo non deve alterare slot già generati
    @Column(name = "capacity_units", nullable = false, precision = 6, scale = 2)
    private BigDecimal capacityUnits;
    
    @Column(name = "current_load", nullable = false, precision = 6, scale = 2)
    private BigDecimal currentLoad = BigDecimal.ZERO;
    
    protected ProductionSlot() {
    }

    public ProductionSlot(Station station, LocalDateTime slotStart, LocalDateTime slotEnd, BigDecimal capacityUnits) {
        this.station = station;
        this.slotStart = slotStart;
        this.slotEnd = slotEnd;
        this.capacityUnits = capacityUnits;
    }

    public Long getId() { return id; }
    public Station getStation() { return station; }
    public LocalDateTime getSlotStart() { return slotStart; }
    public LocalDateTime getSlotEnd() { return slotEnd; }
    public BigDecimal getCapacityUnits() { return capacityUnits; }
    public BigDecimal getCurrentLoad() { return currentLoad; }

    public void setCurrentLoad(BigDecimal currentLoad) { this.currentLoad = currentLoad; }
}
