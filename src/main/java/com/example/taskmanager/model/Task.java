package com.example.taskmanager.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private TaskUser user;

    private String title;
    private String description;
    private boolean completed;
    private LocalDate deadline;

    // Tom konstruktor krävs av JPA
    public Task() {
    }

    public Task(String title, String description, boolean completed, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.deadline = deadline;
    }

    // Getters och setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public TaskUser getUser() {
        return user;
    }

    public void setUser(TaskUser user) {
        this.user = user;
    }
}