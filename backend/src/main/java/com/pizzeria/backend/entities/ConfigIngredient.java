package com.pizzeria.backend.entities;

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
@Table(name = "config_ingredient")
public class ConfigIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "config_id", nullable = false)
    private OrderItemConfig config;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "final_quantity", nullable = false)
    private Integer finalQuantity;

    protected ConfigIngredient() {
    }

    public ConfigIngredient(OrderItemConfig config, Ingredient ingredient, Integer finalQuantity) {
        this.config = config;
        this.ingredient = ingredient;
        this.finalQuantity = finalQuantity;
    }

    public Long getId() { return id; }
    public OrderItemConfig getConfig() { return config; }
    public Ingredient getIngredient() { return ingredient; }
    public Integer getFinalQuantity() { return finalQuantity; }

    public void setFinalQuantity(Integer finalQuantity) { this.finalQuantity = finalQuantity; }
}
