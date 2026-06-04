package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.taskmanager.model.Category;
import com.example.taskmanager.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.Set;

@Service
public class TaskService {

	private final TaskRepository taskRepository;


	private final CategoryRepository categoryRepository;
	// Konstruktor - Spring skickar automatiskt in repository här
	public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
	    this.taskRepository = taskRepository;
	    this.categoryRepository = categoryRepository;
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
	    validateTask(task);
	    return taskRepository.save(task);
	}

	// Ta bort en uppgift via id
	public void deleteTask(Long id) {
		taskRepository.deleteById(id);
	}

	public List<Task> getTasksByUserId(Long userid) {
		return taskRepository.findByUserUserid(userid);
	}

	public List<Task> findByUserUseridAndCompleted(Long userid, boolean completed) {
		return taskRepository.findByUserUseridAndCompleted(userid, completed);
	}
	
	public List<Task> findByUserUseridAndDeadlineBefore(Long userid, LocalDate deadline) {
	    return taskRepository.findByUserUseridAndDeadlineBefore(userid, deadline);
	}
	
	public List<Task> getTasksByUserId(Long userid, Sort sort) {
	    return taskRepository.findByUserUserid(userid, sort);
	}
	
	public List<Task> findByUserUseridAndPriority(Long userid, Priority priority) {
	    return taskRepository.findByUserUseridAndPriority(userid, priority);
	}
	
	public Task assignCategories(Task task, List<Long> categoryIds, Long userid) {
	    if (categoryIds == null || categoryIds.isEmpty()) {
	        task.setCategories(new HashSet<>());
	        return task;
	    }
	    Set<Category> categories = new HashSet<>();
	    for (Long categoryId : categoryIds) {
	        Category category = categoryRepository.findByIdAndUserUserid(categoryId, userid)
	                .orElseThrow(() -> new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST, 
	                        "Category not found or not yours: " + categoryId));
	        categories.add(category);
	    }
	    task.setCategories(categories);
	    return task;
	}
	
	private void validateTask(Task task) {
	    if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST, "Title is required");
	    }
	    if (task.getTitle().length() > 200) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST, "Title is too long (max 200 characters)");
	    }
	    if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDate.now())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST, "Deadline cannot be in the past");
	    }
	}
	
	public Task getTaskByIdForUser(Long id, Long userid) {
	    return taskRepository.findByIdAndUserUserid(id, userid)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND, "Task not found"));
	}
}
