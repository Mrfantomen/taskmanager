package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Konstruktor - Spring skickar automatiskt in repository här
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Hämta alla uppgifter
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Hämta en uppgift via id
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Skapa eller uppdatera en uppgift
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    // Ta bort en uppgift via id
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    public List<Task> getTasksByUserId(Long userid) {
        return taskRepository.findByUserUserid(userid);
    }
}