package com.pizzeria.backend.entities;

import java.math.BigDecimal;

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
	    name = "product_stage",
	    uniqueConstraints = @UniqueConstraint(
	        name = "uq_product_sequence",
	        columnNames = {"product_id", "sequence_order"}
	    )
	)
public class ProductStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "fixed_minutes", precision = 4, scale = 1)
    private BigDecimal fixedMinutes;
	
	protected ProductStage() {
	}
	
	 public ProductStage(Product product, Station station, Integer sequenceOrder) {
	        this.product = product;
	        this.station = station;
	        this.sequenceOrder = sequenceOrder;
	    }
	 
	 public Long getId() { return id; }
	    public Product getProduct() { return product; }
	    public Station getStation() { return station; }
	    public Integer getSequenceOrder() { return sequenceOrder; }
	    public BigDecimal getFixedMinutes() { return fixedMinutes; }

	    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }
	    public void setFixedMinutes(BigDecimal fixedMinutes) { this.fixedMinutes = fixedMinutes; }
	}
