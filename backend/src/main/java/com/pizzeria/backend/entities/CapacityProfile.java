package com.pizzeria.backend.entities;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "capacity_profile")
public class CapacityProfile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
	
	@Column(name = "day_of_week", nullable = false)
	private Integer dayOfWeek;
	
	@Column(name="time_from", nullable = false)
	private LocalTime timeFrom;
	
	@Column(name="time_to", nullable = false)
	private LocalTime timeTo;
	
	@Column(name="capacity_per_slot", nullable = false, precision = 6, scale = 2)
    private BigDecimal capacityPerSlot;
	
	@Column(length = 100)
	private String note;

	protected CapacityProfile() {
    }
	
	public CapacityProfile(Station station, Integer dayOfWeek, LocalTime timeFrom, LocalTime timeTo, BigDecimal capacityPerSlot) {
        this.station = station;
        this.dayOfWeek = dayOfWeek;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.capacityPerSlot = capacityPerSlot;
    }
	
	public Long getId() { return id; }
    public Station getStation() { return station; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public LocalTime getTimeFrom() { return timeFrom; }
    public LocalTime getTimeTo() { return timeTo; }
    public BigDecimal getCapacityPerSlot() { return capacityPerSlot; }
    public String getNote() { return note; }
    
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setTimeFrom(LocalTime timeFrom) { this.timeFrom = timeFrom; }
    public void setTimeTo(LocalTime timeTo) { this.timeTo = timeTo; }
    public void setCapacityPerSlot(BigDecimal capacityPerSlot) { this.capacityPerSlot = capacityPerSlot; }
    public void setNote(String note) { this.note = note; }
}
