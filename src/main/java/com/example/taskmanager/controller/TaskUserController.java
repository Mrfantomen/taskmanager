package com.example.taskmanager.controller;


import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.TaskUserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class TaskUserController {

    private final TaskUserService taskUserService;

    public TaskUserController(TaskUserService taskUserService) {
        this.taskUserService = taskUserService;
    }

    @GetMapping
    public List<TaskUser> getAllUsers() {
    	return taskUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public TaskUser getUserById(@PathVariable Long id) {
        return taskUserService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
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