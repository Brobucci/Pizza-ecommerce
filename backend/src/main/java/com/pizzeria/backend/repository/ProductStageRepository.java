package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.ProductStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStageRepository extends JpaRepository<ProductStage, Long> {

    List<ProductStage> findByProductIdOrderBySequenceOrderAsc(Long productId);
}