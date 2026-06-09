package com.example.taskmanager.controller;

import com.example.taskmanager.dto.CategoryResponse;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.AuthService;
import com.example.taskmanager.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthService authService;

    public CategoryController(CategoryService categoryService, AuthService authService) {
        this.categoryService = categoryService;
        this.authService = authService;
    }

    @GetMapping
    public List<CategoryResponse> getMyCategories() {
        TaskUser currentUser = authService.getCurrentUser();
        return categoryService.toResponseList(
                categoryService.getCategoriesForUser(currentUser.getUserid()));
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        return categoryService.toResponse(
                categoryService.getCategoryForUser(id, currentUser.getUserid()));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Category category) {
        TaskUser currentUser = authService.getCurrentUser();
        CategoryResponse saved = categoryService.toResponse(
                categoryService.createCategory(category, currentUser));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        categoryService.deleteCategory(id, currentUser.getUserid());
        return ResponseEntity.noContent().build();
    }
}