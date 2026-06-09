package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.AuthService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.validation.SortValidator;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.example.taskmanager.validation.TaskSpecification;
import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.domain.Specification;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;
	private final AuthService authService;

	private static final SortValidator TASK_SORT_VALIDATOR = new SortValidator(
			java.util.Set.of("id", "title", "deadline", "completed"));

	public TaskController(TaskService taskService, AuthService authService) {
		this.taskService = taskService;
		this.authService = authService;
	}

	@GetMapping
	public List<TaskResponse> getAllOwnTasks(
	        @RequestParam(required = false) Boolean completed,
	        @RequestParam(required = false) LocalDate dueBefore,
	        @RequestParam(required = false) Priority priority,
	        @RequestParam(required = false) String sortBy) {
	    TaskUser currentUser = authService.getCurrentUser();
	    Long userId = currentUser.getUserid();

	    // Börja alltid med user-filter — ingen task utan ägarskap
	    Specification<Task> spec = Specification
	            .where(TaskSpecification.belongsToUser(userId));

	    // Lägg till valfria filter dynamiskt
	    if (completed != null) {
	        spec = spec.and(TaskSpecification.hasCompleted(completed));
	    }
	    if (dueBefore != null) {
	        spec = spec.and(TaskSpecification.hasDeadlineBefore(dueBefore));
	    }
	    if (priority != null) {
	        spec = spec.and(TaskSpecification.hasPriority(priority));
	    }

	    // Sortering hanteras separat
	    List<Task> tasks;
	    if (sortBy != null) {
	        TASK_SORT_VALIDATOR.validate(sortBy);
	        tasks = taskService.findBySpecification(
	                spec, org.springframework.data.domain.Sort.by(sortBy));
	    } else {
	        tasks = taskService.findBySpecification(spec);
	    }
	    return taskService.toResponseList(tasks);
	}



	@PostMapping
	public TaskResponse createTask(@RequestBody TaskRequest request) {
		TaskUser currentUser = authService.getCurrentUser();
		com.example.taskmanager.model.Task task = new com.example.taskmanager.model.Task();
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setCompleted(request.isCompleted());
		task.setDeadline(request.getDeadline());
		task.setPriority(request.getPriority());
		task.setUser(currentUser);
		taskService.assignCategories(task, request.getCategoryIds(), currentUser.getUserid());
		return taskService.toResponse(taskService.saveTask(task));
	}

	@PutMapping("/{id}")
	public TaskResponse updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
		TaskUser currentUser = authService.getCurrentUser();
		com.example.taskmanager.model.Task task = taskService.getTaskByIdForUser(id, currentUser.getUserid());
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setCompleted(request.isCompleted());
		task.setDeadline(request.getDeadline());
		task.setPriority(request.getPriority());
		taskService.assignCategories(task, request.getCategoryIds(), currentUser.getUserid());
		return taskService.toResponse(taskService.saveTask(task));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
	    TaskUser currentUser = authService.getCurrentUser();
	    taskService.deleteTask(id, currentUser.getUserid());
	    return ResponseEntity.noContent().build();
	}
}