package com.group.cover_hub.service;

import com.group.cover_hub.model.dto.request.CategoryRequest;
import com.group.cover_hub.model.dto.response.CategoryResponse;
import com.group.cover_hub.model.entity.Category;
import com.group.cover_hub.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CrudLookupService lookup;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(lookup.category(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setCategoryTitle(request.getCategoryTitle());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = lookup.category(id);
        category.setCategoryTitle(request.getCategoryTitle());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        categoryRepository.delete(lookup.category(id));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryTitle(category.getCategoryTitle())
                .build();
    }
}
