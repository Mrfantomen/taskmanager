package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.validation.SortValidator;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.AuthService;
import com.example.taskmanager.dto.TaskRequest;


import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;
    
    private static final SortValidator TASK_SORT_VALIDATOR =
            new SortValidator(java.util.Set.of("id", "title", "deadline", "completed"));

    // Konstruktor - Spring skickar in servicen automatiskt (samma mönster som i TaskService)
    public TaskController(TaskService taskService, AuthService authService) {
        this.taskService = taskService;
        this.authService = authService;
    }

    // GET /tasks - hämta alla uppgifter
   // @GetMapping
 /*   public List<Task> getAllTasks() {
        TaskUser currentUser = authService.getCurrentUser();
        return taskService.getTasksByUserId(currentUser.getUserid());
    } */
    

    // GET /tasks/{id} - hämta en uppgift via id
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        return taskService.getTaskByIdForUser(id, currentUser.getUserid());
    }

    // POST /tasks - skapa en ny uppgift
    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        TaskUser currentUser = authService.getCurrentUser();
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        task.setUser(currentUser);
        taskService.assignCategories(task, request.getCategoryIds(), currentUser.getUserid());
        return taskService.saveTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        TaskUser currentUser = authService.getCurrentUser();
        // Hämta befintlig task säkert — kastar 404 om den inte finns eller inte tillhör användaren
        Task task = taskService.getTaskByIdForUser(id, currentUser.getUserid());
        // Mutera tillåtna fält på den befintliga instansen
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        taskService.assignCategories(task, request.getCategoryIds(), currentUser.getUserid());
        return taskService.saveTask(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        TaskUser currentUser = authService.getCurrentUser();
        // Verifierar ägarskap — kastar 404 om tasken inte finns eller inte tillhör användaren
        taskService.getTaskByIdForUser(id, currentUser.getUserid());
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }    
    
    @GetMapping
    public List<Task> getAllOwnTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) LocalDate dueBefore,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String sortBy) {
    	System.out.println(">>> getAllOwnTasks - completed=" + completed + " priority=" + priority + " sortBy=" + sortBy);
        TaskUser currentUser = authService.getCurrentUser();
        Long userId = currentUser.getUserid();

        if (completed != null) {
            return taskService.findByUserUseridAndCompleted(userId, completed);
        }
        if (dueBefore != null) {
            return taskService.findByUserUseridAndDeadlineBefore(userId, dueBefore);
        }
        if (priority != null) {
            return taskService.findByUserUseridAndPriority(userId, priority);
        }
        if (sortBy != null) {
            TASK_SORT_VALIDATOR.validate(sortBy);
            return taskService.getTasksByUserId(userId, Sort.by(sortBy));
        }
        return taskService.getTasksByUserId(userId);
    }

}