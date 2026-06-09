package com.example.taskmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.repository.TaskUserRepository;

@Service
public class TaskUserService {
	private final TaskUserRepository taskUserRepository;
	
	   // Konstruktor - Spring skickar automatiskt in repository här
    public TaskUserService(TaskUserRepository taskUserRepository) {
        this.taskUserRepository = taskUserRepository;
    }

    public List<TaskUser> getAllUsers() {
        return taskUserRepository.findAll();
    }


    public Optional<TaskUser> getUserById(Long id) {
        return taskUserRepository.findById(id);
    }

 
    public TaskUser saveUser(TaskUser user) {
        return taskUserRepository.save(user);
    }


    public void deleteUser(Long id) {
        taskUserRepository.deleteById(id);
    }
}


