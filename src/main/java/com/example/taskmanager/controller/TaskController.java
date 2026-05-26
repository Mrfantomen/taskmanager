package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    // Konstruktor - Spring skickar in servicen automatiskt (samma mönster som i TaskService)
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /tasks - hämta alla uppgifter
    @GetMapping
    public List<Task> getAllTasks() {
    	return taskService.getAllTasks();
    }

    // GET /tasks/{id} - hämta en uppgift via id
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    // POST /tasks - skapa en ny uppgift
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        // TODO: spara uppgiften via servicen och returnera den sparade
    	Task t = taskService.saveTask(task);
		return t;
    }

    // PUT /tasks/{id} - uppdatera en befintlig uppgift
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskService.saveTask(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}