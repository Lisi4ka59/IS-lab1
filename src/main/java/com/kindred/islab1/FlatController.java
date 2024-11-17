package com.kindred.islab1;

import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.exceptions.ResourceNotFoundException;
import com.kindred.islab1.services.FlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Flat> createFlat(@RequestBody Flat flat) {
        Flat createdFlat = flatService.createFlat(flat);
        return new ResponseEntity<>(createdFlat, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flat> getFlat(@PathVariable Long id) {
        try {
            Flat flat = flatService.getFlat(id);
            return new ResponseEntity<>(flat, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flat> updateFlat(@PathVariable Long id, @RequestBody Flat flatDetails) {
        try {
            Flat updatedFlat = flatService.updateFlat(id, flatDetails);
            return new ResponseEntity<>(updatedFlat, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        try {
            flatService.deleteFlat(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/max-area")
    public ResponseEntity<Flat> getFlatWithMaxArea() {
        try {
            Flat flat = flatService.getFlatWithMaxArea();
            return new ResponseEntity<>(flat, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/averageNumberOfRooms")
    public ResponseEntity<Float> getAverageNumberOfRooms() {
        try {
            Float averageNumber = flatService.averageNumberOfRooms();
            return new ResponseEntity<>(averageNumber, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/count-is-new-less-than/{value}")
    public ResponseEntity<Long> countFlatsWithIsNewLessThan(@PathVariable boolean value) {
        long count = flatService.countFlatsWithIsNewLessThan(value);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/most-expensive")
    public ResponseEntity<Flat> getMostExpensiveFlat(@RequestBody List<Long> ids) {
        try {
            Flat flat = flatService.getMostExpensiveFlat(ids);
            return new ResponseEntity<>(flat, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/most-expensive-without-balcony")
    public ResponseEntity<Flat> getMostExpensiveFlatWithoutBalcony() {
        try {
            Flat flat = flatService.getMostExpensiveFlatWithoutBalcony();
            return new ResponseEntity<>(flat, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/flat-list")
    public ResponseEntity<List<Flat>> getFlatList() {
        try {
            List<Flat> flats = flatService.getAllFlats();
            System.out.println("of");
            return new ResponseEntity<>(flats, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
