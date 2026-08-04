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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_delivery_queue")
public class OrderDeliveryQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private ProductionSlot slot;

    @Column(name = "load_units", nullable = false, precision = 4, scale = 2)
    private BigDecimal loadUnits = BigDecimal.ONE;

    // massimo tra gli stage_end di tutti gli item dell'ordine,
    // non il singolo item — la consegna parte solo quando TUTTO è pronto
    @Column(name = "kitchen_ready_at", nullable = false)
    private LocalDateTime kitchenReadyAt;

    @Column(name = "estimated_delivered_at", nullable = false)
    private LocalDateTime estimatedDeliveredAt;

    protected OrderDeliveryQueue() {
    }

    public OrderDeliveryQueue(Orders orders, ProductionSlot slot,
                                LocalDateTime kitchenReadyAt, LocalDateTime estimatedDeliveredAt) {
        this.orders = orders;
        this.slot = slot;
        this.kitchenReadyAt = kitchenReadyAt;
        this.estimatedDeliveredAt = estimatedDeliveredAt;
    }

    public Long getId() { return id; }
    public Orders getOrders() { return orders; }
    public ProductionSlot getSlot() { return slot; }
    public BigDecimal getLoadUnits() { return loadUnits; }
    public LocalDateTime getKitchenReadyAt() { return kitchenReadyAt; }
    public LocalDateTime getEstimatedDeliveredAt() { return estimatedDeliveredAt; }

    public void setLoadUnits(BigDecimal loadUnits) { this.loadUnits = loadUnits; }
    public void setEstimatedDeliveredAt(LocalDateTime estimatedDeliveredAt) { this.estimatedDeliveredAt = estimatedDeliveredAt; }
}
