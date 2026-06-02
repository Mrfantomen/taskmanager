package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskUser;
import com.example.taskmanager.repository.TaskUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaskUserRepository taskUserRepository;

    public CustomUserDetailsService(TaskUserRepository taskUserRepository) {
        this.taskUserRepository = taskUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaskUser user = taskUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}