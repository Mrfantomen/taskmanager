package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskUserResponse;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.service.TaskUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class TaskUserController {

    private final TaskUserService taskUserService;

    public TaskUserController(TaskUserService taskUserService) {
        this.taskUserService = taskUserService;
    }

    // POST /users — skapa en ny användare
    @PostMapping
    public TaskUserResponse createUser(@RequestBody TaskUser taskUser) {
        return TaskUserResponse.from(taskUserService.saveUser(taskUser));
    }

    // PUT /users/{id} — uppdatera en befintlig användare
    @PutMapping("/{id}")
    public TaskUserResponse updateUser(@PathVariable Long id,
                                       @RequestBody TaskUser taskUser) {
        taskUser.setUserid(id);
        return TaskUserResponse.from(taskUserService.saveUser(taskUser));
    }

    // DELETE /users/{id} — ta bort en användare
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        taskUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}