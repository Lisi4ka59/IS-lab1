package com.kindred.islab1.services;


import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.exceptions.ResourceNotFoundException;
import com.kindred.islab1.repositories.FlatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    @Autowired
    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    public Flat createFlat(Flat flat) {
        return flatRepository.save(flat);
    }

    public Flat getFlat(Long id) {
        return flatRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
    }

    public Flat updateFlat(Long id, Flat flatDetails) {
        Flat flat = getFlat(id);
        flat.setName(flatDetails.getName());
        flat.setCoordinates(flatDetails.getCoordinates());
        flat.setArea(flatDetails.getArea());
        flat.setPrice(flatDetails.getPrice());
        flat.setBalcony(flatDetails.isBalcony());
        flat.setTimeToMetroOnFoot(flatDetails.getTimeToMetroOnFoot());
        flat.setNumberOfRooms(flatDetails.getNumberOfRooms());
        flat.setIsNew(flatDetails.getIsNew());
        flat.setFurnish(flatDetails.getFurnish());
        flat.setView(flatDetails.getView());
        flat.setHouse(flatDetails.getHouse());
        return flatRepository.save(flat);
    }

    public void deleteFlat(Long id) {
        flatRepository.deleteById(id);
    }

    public float averageNumberOfRooms() {
        return (float) flatRepository.findAll().stream()
                .mapToLong(Flat::getNumberOfRooms)
                .average()
                .orElse(0.0);
    }

    public Flat getFlatWithMaxArea() {
        return flatRepository.findAll().stream()
                .max(Comparator.comparingDouble(Flat::getArea))
                .orElseThrow(() -> new ResourceNotFoundException("No flats available"));
    }

    public long countFlatsWithIsNewLessThan(boolean isNewValue) {
        return flatRepository.findAll().stream()
                .filter(flat -> flat.getIsNew() == isNewValue)
                .count();
    }

    public Flat getMostExpensiveFlat(List<Long> ids) {
        return ids.stream()
                .map(this::getFlat)
                .max(Comparator.comparingDouble(Flat::getPrice))
                .orElseThrow(() -> new ResourceNotFoundException("No flats found with the given IDs"));
    }

    public Flat getMostExpensiveFlatWithoutBalcony() {
        return flatRepository.findAll().stream()
                .filter(flat -> !flat.isBalcony())
                .max(Comparator.comparingDouble(Flat::getPrice))
                .orElseThrow(() -> new ResourceNotFoundException("No flats without a balcony found"));
    }
}
