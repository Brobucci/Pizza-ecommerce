package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.CapacityProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapacityProfileRepository extends JpaRepository<CapacityProfile, Long> {

    List<CapacityProfile> findByStationIdAndDayOfWeek(Long stationId, Integer dayOfWeek);
}