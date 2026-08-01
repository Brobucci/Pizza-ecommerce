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

@Entity
@Table(name = "product")
public class Product {
	
	@Id 
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(name = "base_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal basePrice;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "is_configurable")
	private Boolean isConfigurable = true;
	
	// decisione editoriale permanente: prodotto tolto dal menu,
    // ma resta in DB per non rompere le FK degli ordini storici
    @Column(name = "is_active")
    private Boolean isActive = true;

    // stato del giorno: prodotto normalmente in menu ma oggi esaurito
    // (concetto indipendente da isActive, non va sovrapposto)
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    @Column(name = "finishing_minutes", nullable = false, precision = 4, scale = 1)
    private BigDecimal finishingMinutes = BigDecimal.ZERO;

    protected Product() {
    }

    public Product(Category category, String name, BigDecimal basePrice) {
        this.category = category;
        this.name = name;
        this.basePrice = basePrice;
    }

    // getters
    public Long getId() { return id; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getDescription() { return description; }
    public Boolean getIsConfigurable() { return isConfigurable; }
    public Boolean getIsActive() { return isActive; }
    public Boolean getIsAvailable() { return isAvailable; }
    public BigDecimal getFinishingMinutes() { return finishingMinutes; }

    // setters (solo sui campi che ha senso modificare dopo la creazione)
    public void setName(String name) { this.name = name; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public void setDescription(String description) { this.description = description; }
    public void setIsConfigurable(Boolean isConfigurable) { this.isConfigurable = isConfigurable; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public void setFinishingMinutes(BigDecimal finishingMinutes) { this.finishingMinutes = finishingMinutes; }
}
