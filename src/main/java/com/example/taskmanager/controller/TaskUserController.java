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

    @GetMapping
    public List<TaskUser> getAllUsers() {
    	return taskUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public TaskUser getTaskUserById(@PathVariable Long id) {
        return taskUserService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }
    
    @GetMapping("/{userId}/tasks")
    public List<Task> getTasksForUser(@PathVariable Long userId) {
        return taskService.getTasksByUserId(userId);
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
}