package com.example.taskmanager.dto;

import com.example.taskmanager.model.Category;

public class CategoryResponse {

    private Long id;
    private String name;
    private String color;

    public static CategoryResponse from(Category category) {
        if (category == null) return null;
        CategoryResponse dto = new CategoryResponse();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.color = category.getColor();
        return dto;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
}