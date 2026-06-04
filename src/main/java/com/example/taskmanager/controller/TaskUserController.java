package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.TaskUserService;
import com.example.taskmanager.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class TaskUserController {

	private final TaskUserService taskUserService;
	private final TaskService taskService;

	public TaskUserController(TaskUserService taskUserService, TaskService taskService) {
		this.taskUserService = taskUserService;
		this.taskService = taskService;
	}

	@PostMapping
	public TaskUser createUser(@RequestBody TaskUser taskUser) {
		TaskUser t = taskUserService.saveUser(taskUser);
		return t;
	}

	// PUT /tasks/{id} - uppdatera en befintlig användare
	@PutMapping("/{id}")
	public TaskUser updateUser(@PathVariable Long id, @RequestBody TaskUser taskUser) {
		taskUser.setUserid(id);
		return taskUserService.saveUser(taskUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		taskUserService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	// TODO: Step 7 - These endpoints require an admin role before they can be
	// re-enabled.
	// User enumeration and cross-user data access are security risks without proper
	// role-based access control.

	// GET /users - lists all users (user enumeration risk)
	@GetMapping
	public List<TaskUser> getAllUsers() {
		// return taskUserService.getAllUsers();
		throw new ResponseStatusException(HttpStatus.FORBIDDEN,
				"Admin only - not yet implemented. See Step 7 in Roadmap.");
	}

	// GET /users/{id} - exposes any user by id
	@GetMapping("/{id}")
	public TaskUser getTaskUserById(@PathVariable Long id) {

		/*
		 * return taskUserService.getUserById(id) .orElseThrow(() -> new
		 * ResponseStatusException( HttpStatus.NOT_FOUND, "User not found"));
		 */
		throw new ResponseStatusException(HttpStatus.FORBIDDEN,
				"Admin only - not yet implemented. See Step 7 in Roadmap.");
	}

	// GET /users/{id}/tasks - exposes another user's tasks
	@GetMapping("/{userId}/tasks")
	public List<Task> getTasksForUser(@PathVariable Long userId) {
		// return taskService.getTasksByUserId(userId);
		throw new ResponseStatusException(HttpStatus.FORBIDDEN,
				"Admin only - not yet implemented. See Step 7 in Roadmap.");
	}
}