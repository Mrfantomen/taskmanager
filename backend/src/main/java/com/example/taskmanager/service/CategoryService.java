package com.example.taskmanager.service;

import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.taskmanager.dto.CategoryResponse;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lista alla kategorier för en användare
    public List<Category> getCategoriesForUser(Long userid) {
        return categoryRepository.findByUserUserid(userid);
    }

    // Hämta en specifik kategori, men bara om den tillhör användaren
    public Category getCategoryForUser(Long categoryId, Long userid) {
        return categoryRepository.findByIdAndUserUserid(categoryId, userid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"));
    }

    // Skapa en ny kategori för en användare
    public Category createCategory(Category category, TaskUser owner) {
        // Förhindra dubletter: samma användare får inte ha två kategorier med samma namn
        if (categoryRepository.findByNameAndUserUserid(category.getName(), owner.getUserid()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Category with this name already exists");
        }
        category.setUser(owner);
        return categoryRepository.save(category);
    }

    // Ta bort en kategori, men bara om den tillhör användaren
    public void deleteCategory(Long categoryId, Long userid) {
        Category category = getCategoryForUser(categoryId, userid);
        categoryRepository.delete(category);
    }
    
    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.from(category);
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }
}