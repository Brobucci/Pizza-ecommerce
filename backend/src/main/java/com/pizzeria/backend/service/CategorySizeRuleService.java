package com.pizzeria.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pizzeria.backend.entity.AdjustmentType;
import com.pizzeria.backend.entity.Category;
import com.pizzeria.backend.entity.CategorySizeRule;
import com.pizzeria.backend.entity.Size;
import com.pizzeria.backend.exception.BusinessRuleViolationException;
import com.pizzeria.backend.exception.DuplicateResourceException;
import com.pizzeria.backend.exception.ResourceNotFoundException;
import com.pizzeria.backend.repository.CategorySizeRuleRepository;

@Service
@Transactional(readOnly = true)
public class CategorySizeRuleService {

    private final CategorySizeRuleRepository ruleRepository;
    private final CategoryService categoryService;

    public CategorySizeRuleService(CategorySizeRuleRepository ruleRepository,
                                   CategoryService categoryService) {
        this.ruleRepository = ruleRepository;
        this.categoryService = categoryService;
    }

    public List<CategorySizeRule> findByCategory(Long categoryId) {
        categoryService.findById(categoryId);
        return ruleRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public CategorySizeRule create(Long categoryId, Size size,
                                   AdjustmentType type, BigDecimal value) {
        Category category = categoryService.findById(categoryId);
        validateValue(type, value);

        if (ruleRepository.existsByCategoryIdAndSize(categoryId, size)) {
            throw new DuplicateResourceException(
                    "CategorySizeRule", "category/size", categoryId + "/" + size);
        }
        return ruleRepository.save(new CategorySizeRule(category, size, type, value));
    }

    @Transactional
    public CategorySizeRule update(Long ruleId, AdjustmentType type, BigDecimal value) {
        CategorySizeRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("CategorySizeRule", ruleId));
        validateValue(type, value);

        rule.setAdjustmentType(type);
        rule.setValue(value);
        return rule;
    }

    @Transactional
    public void delete(Long ruleId) {
        CategorySizeRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("CategorySizeRule", ruleId));
        ruleRepository.delete(rule);
    }

    public BigDecimal calculatePrice(Long categoryId, Size size, BigDecimal basePrice) {
        CategorySizeRule rule = ruleRepository.findByCategoryIdAndSize(categoryId, size)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Size " + size + " is not offered for category " + categoryId));

        BigDecimal price = rule.apply(basePrice);
        if (price.signum() < 0) {
            throw new BusinessRuleViolationException(
                    "Rule for category " + categoryId + " and size " + size
                            + " produces a negative price");
        }
        return price;
    }

    private void validateValue(AdjustmentType type, BigDecimal value) {
        if (type == AdjustmentType.MULTIPLIER && value.signum() <= 0) {
            throw new BusinessRuleViolationException(
                    "A MULTIPLIER value must be greater than zero");
        }
    }
}