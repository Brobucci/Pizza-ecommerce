package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.Orders;
import com.pizzeria.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByStatusOrderByRequestedTimeAsc(OrderStatus status);

    List<Orders> findByStatusInOrderByRequestedTimeAsc(List<OrderStatus> statuses);

    List<Orders> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Orders> findByRequestedTimeBetween(LocalDateTime from, LocalDateTime to);
}