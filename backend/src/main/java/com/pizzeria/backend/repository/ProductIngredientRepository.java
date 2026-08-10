package com.pizzeria.backend.repository;

import com.pizzeria.backend.entity.ProductIngredient;
import com.pizzeria.backend.entity.ProductIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, ProductIngredientId> {

    @Query("SELECT pi FROM ProductIngredient pi JOIN FETCH pi.ingredient WHERE pi.product.id = :productId")
    List<ProductIngredient> findByProductIdWithIngredient(@Param("productId") Long productId);
}