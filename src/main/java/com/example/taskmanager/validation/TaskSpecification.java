package com.example.taskmanager.validation;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

    // Privat konstruktor — alla metoder är statiska, klassen ska aldrig instansieras
    private TaskSpecification() {}

    public static Specification<Task> belongsToUser(Long userid) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("userid"), userid);
    }

    public static Specification<Task> hasCompleted(boolean completed) {
        return (root, query, cb) ->
                cb.equal(root.get("completed"), completed);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, cb) ->
                cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasDeadlineBefore(LocalDate date) {
        return (root, query, cb) ->
                cb.lessThan(root.get("deadline"), date);
    }
}