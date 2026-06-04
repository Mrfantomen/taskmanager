package com.example.taskmanager.config;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.model.Category;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.repository.CategoryRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.TaskUserRepository;
import com.example.taskmanager.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Set;

@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedData(AuthService authService,
                                      TaskUserRepository taskUserRepository,
                                      TaskRepository taskRepository,
                                      CategoryRepository categoryRepository) {
        return args -> {
            createUserIfMissing(authService, "johan", "hemligt123");
            createUserIfMissing(authService, "anna", "annapass123");

            taskUserRepository.findByUsername("johan").ifPresent(johan -> {
                Category work = createCategoryIfMissing(categoryRepository, "work", "#FF5733", johan);
                Category personal = createCategoryIfMissing(categoryRepository, "personal", "#33C7FF", johan);
                createWelcomeTaskIfMissing(taskRepository, johan, work, personal);
            });

            taskUserRepository.findByUsername("anna").ifPresent(anna ->
                createAnnasTaskIfMissing(taskRepository, anna));

            log.info("Seed data ready: users johan/anna, categories work/personal, sample tasks");
        };
    }

    private void createUserIfMissing(AuthService authService, String username, String password) {
        try {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(username);
            req.setPassword(password);
            authService.register(req);
        } catch (Exception e) {
            // Användaren finns redan
        }
    }

    private Category createCategoryIfMissing(CategoryRepository categoryRepository,
                                              String name, String color, TaskUser owner) {
        return categoryRepository.findByNameAndUserUserid(name, owner.getUserid())
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(name);
                    category.setColor(color);
                    category.setUser(owner);
                    return categoryRepository.save(category);
                });
    }

    private void createWelcomeTaskIfMissing(TaskRepository taskRepository,
                                             TaskUser johan,
                                             Category work,
                                             Category personal) {
        if (taskRepository.findByUserUserid(johan.getUserid()).isEmpty()) {
            Task task = new Task();
            task.setTitle("Welcome task");
            task.setDescription("This is a seed task with priority and categories.");
            task.setCompleted(false);
            task.setDeadline(LocalDate.now().plusDays(7));
            task.setPriority(Priority.HIGH);
            task.setUser(johan);
            task.setCategories(Set.of(work, personal));
            taskRepository.save(task);
        }
    }

    private void createAnnasTaskIfMissing(TaskRepository taskRepository, TaskUser anna) {
        if (taskRepository.findByUserUserid(anna.getUserid()).isEmpty()) {
            Task task = new Task();
            task.setTitle("Anna's first task");
            task.setDescription("A seed task for anna.");
            task.setCompleted(false);
            task.setDeadline(LocalDate.now().plusDays(14));
            task.setPriority(Priority.MEDIUM);
            task.setUser(anna);
            taskRepository.save(task);
        }
    }
}