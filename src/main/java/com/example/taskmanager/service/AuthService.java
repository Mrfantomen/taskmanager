package com.example.taskmanager.service;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.repository.TaskUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class AuthService {

	private final TaskUserRepository taskUserRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(TaskUserRepository taskUserRepository, PasswordEncoder passwordEncoder) {
		this.taskUserRepository = taskUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public TaskUser register(RegisterRequest request) {
		// Kolla att användarnamnet inte redan finns
		if (taskUserRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
		}

		// Skapa ny användare med hashat lösenord
		TaskUser user = new TaskUser();
		user.setUsername(request.getUsername());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

		return taskUserRepository.save(user);
	}

	public TaskUser getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return taskUserRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));
	}
}