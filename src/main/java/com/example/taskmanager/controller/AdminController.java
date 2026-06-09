package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUserResponse;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.TaskUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskUserService taskUserService;
    private final TaskService taskService;

    public AdminController(TaskUserService taskUserService, TaskService taskService) {
        this.taskUserService = taskUserService;
        this.taskService = taskService;
    }

    // GET /admin/users — lista alla användare
    @GetMapping("/users")
    public List<TaskUserResponse> getAllUsers() {
        return taskUserService.getAllUsers()
                .stream()
                .map(TaskUserResponse::from)
                .collect(Collectors.toList());
    }

    // GET /admin/users/{id} — hämta en specifik användare
    @GetMapping("/users/{id}")
    public TaskUserResponse getUserById(@PathVariable Long id) {
        return taskUserService.getUserById(id)
                .map(TaskUserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    // GET /admin/users/{id}/tasks — hämta en användares tasks
    @GetMapping("/users/{userId}/tasks")
    public List<TaskResponse> getTasksForUser(@PathVariable Long userId) {
        return taskService.toResponseList(
                taskService.getTasksByUserId(userId));
    }

    // GET /admin/tasks — lista ALLA tasks i systemet
    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.toResponseList(taskService.getAllTasks());
    }

    // GET /admin/tasks/{id} — hämta valfri task via id
    @GetMapping("/tasks/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(TaskResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));
    }
}