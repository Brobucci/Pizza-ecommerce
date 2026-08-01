package com.pizzeria.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "product_ingredient")
public class ProductIngredient {

    @EmbeddedId
    private ProductIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "standard_quantity")
    private Integer standardQuantity = 1;

    protected ProductIngredient() {
    }

    public ProductIngredient(Product product, Ingredient ingredient, Integer standardQuantity) {
        this.product = product;
        this.ingredient = ingredient;
        this.id = new ProductIngredientId(product.getId(), ingredient.getId());
        this.standardQuantity = standardQuantity;
    }

    public ProductIngredientId getId() { return id; }
    public Product getProduct() { return product; }
    public Ingredient getIngredient() { return ingredient; }
    public Integer getStandardQuantity() { return standardQuantity; }

    public void setStandardQuantity(Integer standardQuantity) {
        this.standardQuantity = standardQuantity;
    }
}