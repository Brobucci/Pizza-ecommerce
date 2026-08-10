package com.pizzeria.backend.repository;
import com.pizzeria.backend.entity.CategorySizeRule;
import com.pizzeria.backend.entity.Size;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorySizeRuleRepository extends JpaRepository<CategorySizeRule, Long>{
	
	Optional<CategorySizeRule> findByCategorySizeRuleIdAndSize(Long id, Size size);
	List<CategorySizeRule> findByCategoryId(Long categoryId);
}
