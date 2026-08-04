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
@Table(name = "order_item_config")
public class OrderItemConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
	
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal fraction = BigDecimal.ONE;
    
    protected OrderItemConfig() {
    }

    public OrderItemConfig(OrderItem orderItem, Product product, BigDecimal fraction) {
        this.orderItem = orderItem;
        this.product = product;
        this.fraction = fraction;
    }

    public Long getId() { return id; }
    public OrderItem getOrderItem() { return orderItem; }
    public Product getProduct() { return product; }
    public BigDecimal getFraction() { return fraction; }

    public void setFraction(BigDecimal fraction) { this.fraction = fraction; }
}
