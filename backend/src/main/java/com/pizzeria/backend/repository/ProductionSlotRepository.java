package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.ProductionSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductionSlotRepository extends JpaRepository<ProductionSlot, Long> {

    List<ProductionSlot> findByStationIdAndSlotStartBetweenOrderBySlotStartAsc(Long stationId, LocalDateTime from, LocalDateTime to);

    Optional<ProductionSlot> findByStationIdAndSlotStart(Long stationId, LocalDateTime slotStart);
}