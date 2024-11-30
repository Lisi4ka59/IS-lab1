package com.kindred.islab1.controllers;

import com.kindred.islab1.services.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ad-flat")
@PreAuthorize("hasRole('USER')")
public class AdvancedFlatController {
    @Autowired
    FlatService flatService;

    @GetMapping("/max-area")
    public ResponseEntity<Map<String, Object>> getFlatWithMaxArea() {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getFlatWithMaxArea());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/averageNumberOfRooms")
    public ResponseEntity<Map<String, Object>> getAverageNumberOfRooms() {
        Map<String, Object> response = new HashMap<>();
        response.put("averageNumberOfRooms", flatService.averageNumberOfRooms());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/count-is-new/{value}")
    public ResponseEntity<Map<String, Object>> countFlatsWithIsNewLessThan(@PathVariable boolean value) {
        Map<String, Object> response = new HashMap<>();
        response.put("count", flatService.countFlatsWithIsNew(value));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/most-expensive")
    public ResponseEntity<Map<String, Object>> getMostExpensiveFlat(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getMostExpensiveFlat(ids));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/most-expensive-without-balcony")
    public ResponseEntity<Map<String, Object>> getMostExpensiveFlatWithoutBalcony() {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getMostExpensiveFlatWithoutBalcony());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
