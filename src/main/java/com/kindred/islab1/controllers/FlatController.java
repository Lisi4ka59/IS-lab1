package com.kindred.islab1.controllers;

import com.kindred.islab1.entities.Coordinates;
import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.entities.House;
import com.kindred.islab1.services.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/flats")
public class FlatController {
    private final FlatService flatService;

    @Autowired
    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Flat flat) {
        Map<String, Object> response = new HashMap<>();

        response.put("flat", flatService.createFlat(flat, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importFlat(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile flatFile) throws IOException {
        return new ResponseEntity<>(flatService.importFlats(flatFile, userDetails.getUsername()), HttpStatus.CREATED);
    }


    @PostMapping("/house")
    public ResponseEntity<Map<String, Object>> createHouse(@AuthenticationPrincipal UserDetails userDetails, @RequestBody House house) {
        Map<String, Object> response = new HashMap<>();
        response.put("house", flatService.createHouse(house, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/coordinates")
    public ResponseEntity<Map<String, Object>> createCoordinates(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Coordinates coordinates) {
        Map<String, Object> response = new HashMap<>();
        response.put("coordinates", flatService.createCoordinates(coordinates, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/houses")
    public ResponseEntity<Map<String, Object>> getHouse() {
        Map<String, Object> response = new HashMap<>();
        response.put("houses", flatService.getHouses());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/coordinates")
    public ResponseEntity<Map<String, Object>> getCoordinates() {
        Map<String, Object> response = new HashMap<>();
        response.put("coordinates", flatService.getCoordinates());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlat(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getFlat(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateFlat(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Flat flatDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("updatedFlat", flatService.updateFlat(flatDetails, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFlat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        flatService.deleteFlat(id, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/flat-list")
    public ResponseEntity<Map<String, Object>> getFlatList() {
        Map<String, Object> response = new HashMap<>();
        response.put("flats", flatService.getAllFlats());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
