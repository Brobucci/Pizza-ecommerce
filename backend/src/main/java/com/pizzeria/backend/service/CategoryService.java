package com.pizzeria.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pizzeria.backend.entity.Category;
import com.pizzeria.backend.exception.BusinessRuleViolationException;
import com.pizzeria.backend.exception.DuplicateResourceException;
import com.pizzeria.backend.exception.ResourceNotFoundException;
import com.pizzeria.backend.repository.CategoryRepository;
import com.pizzeria.backend.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Transactional
    public Category create(String name) {
        String normalized = name.trim();
        if (categoryRepository.existsByNameIgnoreCase(normalized)) {
            throw new DuplicateResourceException("Category", "name", normalized);
        }
        return categoryRepository.save(new Category(normalized));
    }

    @Transactional
    public Category rename(Long id, String newName) {
        Category category = findById(id);
        String normalized = newName.trim();

        boolean sameNameIgnoringCase = category.getName().equalsIgnoreCase(normalized);
        if (!sameNameIgnoringCase && categoryRepository.existsByNameIgnoreCase(normalized)) {
            throw new DuplicateResourceException("Category", "name", normalized);
        }

        category.setName(normalized);
        return category;
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessRuleViolationException(
                    "Category " + id + " cannot be deleted: it still contains products");
        }
        categoryRepository.delete(category);
    }
}