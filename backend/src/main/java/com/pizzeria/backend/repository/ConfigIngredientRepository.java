package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.ConfigIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConfigIngredientRepository extends JpaRepository<ConfigIngredient, Long> {

    @Query("SELECT ci FROM ConfigIngredient ci JOIN FETCH ci.ingredient WHERE ci.config.id = :configId")
    List<ConfigIngredient> findByConfigIdWithIngredient(@Param("configId") Long configId);
}