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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "order_item_stage_queue",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_item_sequence_queue",
        columnNames = {"order_item_id", "sequence_order"}
    )
)
public class OrderItemStageQueue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_item_id", nullable = false)
	private OrderItem orderItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "station_id", nullable = false)
	private Station station;

	@Column(name = "sequence_order", nullable = false)
	private Integer sequenceOrder;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slot_id", nullable = false)
	private ProductionSlot slot;

	// copiato da station_size_load al momento della pianificazione,
	// in base alla taglia dell'order_item — non ricalcolato a runtime
	@Column(name = "load_units", nullable = false, precision = 4, scale = 2)
	private BigDecimal loadUnits;

	@Column(name = "stage_start", nullable = false)
	private LocalDateTime stageStart;

	@Column(name = "stage_end", nullable = false)
	private LocalDateTime stageEnd;

	protected OrderItemStageQueue() {
	}

	public OrderItemStageQueue(OrderItem orderItem, Station station, Integer sequenceOrder, ProductionSlot slot,
			BigDecimal loadUnits, LocalDateTime stageStart, LocalDateTime stageEnd) {
		this.orderItem = orderItem;
		this.station = station;
		this.sequenceOrder = sequenceOrder;
		this.slot = slot;
		this.loadUnits = loadUnits;
		this.stageStart = stageStart;
		this.stageEnd = stageEnd;
	}
	
	public Long getId() { return id; }
    public OrderItem getOrderItem() { return orderItem; }
    public Station getStation() { return station; }
    public Integer getSequenceOrder() { return sequenceOrder; }
    public ProductionSlot getSlot() { return slot; }
    public BigDecimal getLoadUnits() { return loadUnits; }
    public LocalDateTime getStageStart() { return stageStart; }
    public LocalDateTime getStageEnd() { return stageEnd; }

    // niente setter: una volta pianificata, una fase o resta così o viene
    // ripianificata (nuova riga), non "corretta sul posto" — coerente con
}
