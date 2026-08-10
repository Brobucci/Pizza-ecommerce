package com.pizzeria.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pizzeria.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);
	
	List<Product> findByIsActiveTrue();
	
}
