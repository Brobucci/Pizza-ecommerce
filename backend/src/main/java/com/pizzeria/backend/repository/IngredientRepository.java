package com.pizzeria.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pizzeria.backend.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>{
	
	List<Ingredient> findByIsAvailableTrue();	
}
