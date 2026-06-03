package com.example.taskmanager.controller;

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

    // GET /categories — lista alla mina kategorier
    @GetMapping
    public List<Category> getMyCategories() {
        TaskUser currentUser = authService.getCurrentUser();
        return categoryService.getCategoriesForUser(currentUser.getUserid());
    }

    // GET /categories/{id} — hämta en av mina kategorier
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        return categoryService.getCategoryForUser(id, currentUser.getUserid());
    }

    // POST /categories — skapa en ny kategori
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        TaskUser currentUser = authService.getCurrentUser();
        Category saved = categoryService.createCategory(category, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // DELETE /categories/{id} — ta bort en av mina kategorier
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        categoryService.deleteCategory(id, currentUser.getUserid());
        return ResponseEntity.noContent().build();
    }
}