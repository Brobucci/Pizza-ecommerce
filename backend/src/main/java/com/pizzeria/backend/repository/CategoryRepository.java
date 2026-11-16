package com.pizzeria.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.pizzeria.backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	boolean existsByNameIgnoreCase(String normalized);
}


