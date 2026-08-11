package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.OrderItemStageQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemStageQueueRepository extends JpaRepository<OrderItemStageQueue, Long> {

    List<OrderItemStageQueue> findByOrderItemIdOrderBySequenceOrderAsc(Long orderItemId);

    List<OrderItemStageQueue> findBySlotId(Long slotId);
}