package com.example.taskmanager.config;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.TaskUserRepository;
import com.example.taskmanager.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(AuthService authService,
                                      TaskUserRepository taskUserRepository,
                                      TaskRepository taskRepository) {
        return args -> {
            createUserIfMissing(authService, "johan", "hemligt123");
            createUserIfMissing(authService, "anna", "annapass123");
            createSeedTaskIfMissing(taskUserRepository, taskRepository);
            System.out.println(">>> Seed data ready: users johan/anna + sample task <<<");
        };
    }

    private void createUserIfMissing(AuthService authService, String username, String password) {
        try {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(username);
            req.setPassword(password);
            authService.register(req);
        } catch (Exception e) {
            // Användaren finns redan, det är okej
        }
    }

    private void createSeedTaskIfMissing(TaskUserRepository taskUserRepository,
                                         TaskRepository taskRepository) {
        // Skapa bara om databasen är tom på tasks
        if (taskRepository.count() > 0) {
            return;
        }
        taskUserRepository.findByUsername("johan").ifPresent(johan -> {
            Task task = new Task();
            task.setTitle("Welcome task");
            task.setDescription("This is a seed task that belongs to johan.");
            task.setCompleted(false);
            task.setDeadline(LocalDate.now().plusDays(7));
            task.setUser(johan);
            taskRepository.save(task);
        });
    }
}
