package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskUser;

public class TaskUserResponse {

    private Long userid;
    private String username;

    // Statisk fabriksmetod — smidigare än en separat mapper-klass
    public static TaskUserResponse from(TaskUser user) {
        if (user == null) return null;
        TaskUserResponse dto = new TaskUserResponse();
        dto.userid = user.getUserid();
        dto.username = user.getUsername();
        return dto;
    }

    public Long getUserid() { return userid; }
    public String getUsername() { return username; }
}