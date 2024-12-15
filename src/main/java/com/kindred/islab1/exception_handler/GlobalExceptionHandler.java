package com.kindred.islab1.exception_handler;

import com.kindred.islab1.entities.ImportHistory;
import com.kindred.islab1.exceptions.ImportException;
import com.kindred.islab1.exceptions.ResourceNotFoundException;
import com.kindred.islab1.repositories.ImportHistoryRepository;
import com.kindred.islab1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImportHistoryRepository importHistoryRepository;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getReason());
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImportException.class)
    public ResponseEntity<Map<String, String>> handleImportException(ImportException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        ImportHistory importHistory = new ImportHistory();
        importHistory.setStatus(ex.status);
        importHistory.setOwnerId(userRepository.findByUsername(ex.user).orElseThrow().getId());
        importHistory.setUsername(ex.user);
        importHistoryRepository.save(importHistory);
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.httpStatus);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
