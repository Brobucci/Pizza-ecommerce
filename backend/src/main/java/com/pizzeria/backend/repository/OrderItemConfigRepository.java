package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.OrderItemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemConfigRepository extends JpaRepository<OrderItemConfig, Long> {

    @Query("SELECT c FROM OrderItemConfig c JOIN FETCH c.product WHERE c.orderItem.id = :orderItemId")
    List<OrderItemConfig> findByOrderItemIdWithProduct(@Param("orderItemId") Long orderItemId);
}