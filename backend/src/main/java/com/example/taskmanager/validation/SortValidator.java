package com.example.taskmanager.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

public class SortValidator {

    private final Set<String> allowedFields;

    public SortValidator(Set<String> allowedFields) {
        this.allowedFields = allowedFields;
    }

    public void validate(String sortBy) {
        if (sortBy == null) {
            return; // valfri parameter, null betyder "ingen sortering"
        }
        if (!allowedFields.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid sortBy. Allowed values: " + allowedFields);
        }
    }
}
