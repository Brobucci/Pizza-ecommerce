package com.pizzeria.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", nullable = true)
    private AppUser customer;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(length = 255)
    private String address;

    @Column(name = "delivery_notes", length = 255)
    private String deliveryNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 25)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "requested_time", nullable = false)
    private LocalDateTime requestedTime;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Orders() {
    }

    public Orders(String customerName, OrderType orderType, LocalDateTime requestedTime) {
        this.customerName = customerName;
        this.orderType = orderType;
        this.requestedTime = requestedTime;
    }

    // getters
    public Long getId() { return id; }
    public AppUser getCustomer() { return customer; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getContactEmail() { return contactEmail; }
    public String getAddress() { return address; }
    public String getDeliveryNotes() { return deliveryNotes; }
    public OrderType getOrderType() { return orderType; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getRequestedTime() { return requestedTime; }
    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // setters
    public void setCustomer(AppUser customer) { this.customer = customer; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setAddress(String address) { this.address = address; }
    public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
}
