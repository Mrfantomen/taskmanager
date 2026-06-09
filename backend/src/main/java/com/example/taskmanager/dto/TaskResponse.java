package com.example.taskmanager.dto;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDate deadline;
    private Priority priority;
    private TaskUserResponse user;
    private Set<CategoryResponse> categories;

    public static TaskResponse from(Task task) {
        if (task == null) return null;
        TaskResponse dto = new TaskResponse();
        dto.id = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.completed = task.isCompleted();
        dto.deadline = task.getDeadline();
        dto.priority = task.getPriority();
        dto.user = TaskUserResponse.from(task.getUser());
        dto.categories = task.getCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toSet());
        return dto;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public LocalDate getDeadline() { return deadline; }
    public Priority getPriority() { return priority; }
    public TaskUserResponse getUser() { return user; }
    public Set<CategoryResponse> getCategories() { return categories; }
}