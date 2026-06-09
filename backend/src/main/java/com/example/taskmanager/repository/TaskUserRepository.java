package com.example.taskmanager.repository;

import com.example.taskmanager.model.TaskUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {

    Optional<TaskUser> findByUsername(String username);
}