package com.pizzeria.backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
		name = "category_size_rule",
		uniqueConstraints = @UniqueConstraint(
				name = "uq_category_size",
				columnNames = { "category_id", "size"}
				)
		)
public class CategorySizeRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Size size; 
	
	@Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false, length = 10)
    private AdjustmentType adjustmentType;
	
	@Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    protected CategorySizeRule() {
    }

    public CategorySizeRule(Category category, Size size, AdjustmentType adjustmentType, BigDecimal value) {
        this.category = category;
        this.size = size;
        this.adjustmentType = adjustmentType;
        this.value = value;
    }

    public Long getId() { return id; }
    public Category getCategory() { return category; }
    public Size getSize() { return size; }
    public AdjustmentType getAdjustmentType() { return adjustmentType; }
    public BigDecimal getValue() { return value; }

    public void setSize(Size size) { this.size = size; }
    public void setAdjustmentType(AdjustmentType adjustmentType) { this.adjustmentType = adjustmentType; }
    public void setValue(BigDecimal value) { this.value = value; }
}
