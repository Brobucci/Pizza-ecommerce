package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.OrderDeliveryQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDeliveryQueueRepository extends JpaRepository<OrderDeliveryQueue, Long> {

    Optional<OrderDeliveryQueue> findByOrderId(Long orderId);
}
