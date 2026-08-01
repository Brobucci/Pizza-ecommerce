package com.pizzeria.backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient")
public class Ingredient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(name = "extra_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal extraPrice = BigDecimal.ZERO;
	
	@Column(name = "contains_lactose")
	private Boolean containsLactose = false;
	
	@Column(name = "is_removable_with_discount")
	private Boolean isRemovableWithDiscount = false; 
	
	@Column(name = "is_available")
    private Boolean isAvailable = true;
	
	protected Ingredient() {
	}

	public Ingredient(String name, BigDecimal extraPrice) {
        this.name = name;
        this.extraPrice = extraPrice;
    }
	
	public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getExtraPrice() { return extraPrice; }
    public Boolean getContainsLactose() { return containsLactose; }
    public Boolean getIsRemovableWithDiscount() { return isRemovableWithDiscount; }
    public Boolean getIsAvailable() { return isAvailable; }

    public void setName(String name) { this.name = name; }
    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }
    public void setContainsLactose(Boolean containsLactose) { this.containsLactose = containsLactose; }
    public void setIsRemovableWithDiscount(Boolean isRemovableWithDiscount) { this.isRemovableWithDiscount = isRemovableWithDiscount; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
	
}
