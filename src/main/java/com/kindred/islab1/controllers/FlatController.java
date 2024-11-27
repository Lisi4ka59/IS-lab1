package com.kindred.islab1.controllers;

import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.services.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/flats")
@PreAuthorize("hasRole('USER')")
public class FlatController {
    private final FlatService flatService;

    @Autowired
    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlat(@RequestBody Flat flat) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.createFlat(flat));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlat(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getFlat(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFlat(@PathVariable Long id, @RequestBody Flat flatDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.updateFlat(id, flatDetails));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFlat(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        flatService.deleteFlat(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/flat-list")
    public ResponseEntity<Map<String, Object>> getFlatList() {
        Map<String, Object> response = new HashMap<>();
        response.put("flats", flatService.getAllFlats());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
