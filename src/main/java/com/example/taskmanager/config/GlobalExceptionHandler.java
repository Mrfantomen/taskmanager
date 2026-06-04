package com.example.taskmanager.config;

import com.example.taskmanager.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Fångar ResponseStatusException som vi kastar manuellt
    // (t.ex. 404 Task not found, 409 Conflict, 403 Forbidden)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getStatusCode().value(),
                ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // Fångar alla oväntade undantag som inte hanterats explicit
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
 // Fångar konverteringsfel på query parameters och path variables
 // t.ex. ?priority=URGENT när Priority-enum inte har det värdet
 @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
 public ResponseEntity<ErrorResponse> handleTypeMismatch(
         org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
     String message = "Invalid value '" + ex.getValue() +
             "' for parameter '" + ex.getName() + "'";
     ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
 }
}