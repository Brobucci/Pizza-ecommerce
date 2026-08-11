package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.Size;
import com.pizzeria.backend.entity.StationSizeLoad;
import com.pizzeria.backend.entity.StationSizeLoadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationSizeLoadRepository extends JpaRepository<StationSizeLoad, StationSizeLoadId> {

    Optional<StationSizeLoad> findById_StationIdAndId_Size(Long stationId, Size size);

    List<StationSizeLoad> findById_StationId(Long stationId);
}