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

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private Size size = Size.NORMAL;

    @Column(name = "is_lactose_free_base")
    private Boolean isLactoseFreeBase = false;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    protected OrderItem() {
    }

    public OrderItem(Orders orders, BigDecimal unitPrice) {
        this.orders = orders;
        this.unitPrice = unitPrice;
    }

    public Long getId() { return id; }
    public Orders getOrders() { return orders; }
    public Integer getQuantity() { return quantity; }
    public Size getSize() { return size; }
    public Boolean getIsLactoseFreeBase() { return isLactoseFreeBase; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setSize(Size size) { this.size = size; }
    public void setIsLactoseFreeBase(Boolean isLactoseFreeBase) { this.isLactoseFreeBase = isLactoseFreeBase; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
