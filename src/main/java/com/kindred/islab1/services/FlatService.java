package com.kindred.islab1.services;


import com.kindred.islab1.authentication.Roles;
import com.kindred.islab1.entities.Coordinates;
import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.entities.House;
import com.kindred.islab1.entities.User;
import com.kindred.islab1.exceptions.ResourceNotFoundException;
import com.kindred.islab1.repositories.CoordinatesRepository;
import com.kindred.islab1.repositories.FlatRepository;
import com.kindred.islab1.repositories.HouseRepository;
import com.kindred.islab1.repositories.UserRepository;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    @Autowired
    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    @Autowired
    HouseRepository houseRepository;

    @Autowired
    CoordinatesRepository coordinatesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleService roleService;



    public House createHouse(House house, String username) {
        house.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());
        return houseRepository.save(house);
    }

    public List<House> getHouses() {
        return houseRepository.findAllByOrderByNameAsc();
    }

    public Coordinates createCoordinates(Coordinates coordinates, String username) {
        coordinates.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());
        return coordinatesRepository.save(coordinates);
    }

    public List<Coordinates> getCoordinates() {
        return coordinatesRepository.findAllByOrderByIdAsc();
    }

    public Flat createFlat(Flat flat, String username) {
        if (flat.getCoordinates().getId() != null) {
            flat.setCoordinates(coordinatesRepository.findById(flat.getCoordinates().getId()).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find this coordinates")));
        }
        if (flat.getHouse().getId() != null) {
            flat.setHouse(houseRepository.findById(flat.getHouse().getId()).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find this house")));
        }
        flat.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());

        return flatRepository.save(flat);
    }

    public Flat getFlat(Long id) {
        return flatRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
    }

    public Flat updateFlat(Flat flatDetails, String username) {
        Flat flat = getFlat(flatDetails.getId());
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        if (flat.getHouse().getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.getHouse().setName(flatDetails.getHouse().getName());
            flat.getHouse().setNumberOfFlatsOnFloor(flatDetails.getHouse().getNumberOfFlatsOnFloor());
            flat.getHouse().setYear(flatDetails.getHouse().getYear());
        }
        if (flat.getCoordinates().getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.getCoordinates().setX(flatDetails.getCoordinates().getX());
            flat.getCoordinates().setY(flatDetails.getCoordinates().getY());
        }
        if (flat.getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.setName(flatDetails.getName());
            flat.setArea(flatDetails.getArea());
            flat.setPrice(flatDetails.getPrice());
            flat.setBalcony(flatDetails.isBalcony());
            flat.setTimeToMetroOnFoot(flatDetails.getTimeToMetroOnFoot());
            flat.setNumberOfRooms(flatDetails.getNumberOfRooms());
            flat.setIsNew(flatDetails.getIsNew());
            flat.setFurnish(flatDetails.getFurnish());
            flat.setView(flatDetails.getView());
        }
        return flatRepository.save(flat);
    }

    @Transient
    public void deleteFlat(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        Flat flat = getFlat(id);
        if (flat.getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flatRepository.deleteById(id);
            if (!flatRepository.existsByCoordinatesId(flat.getCoordinates().getId())) {
                coordinatesRepository.deleteById(flat.getCoordinates().getId());
            }
            if (!flatRepository.existsByHouseId(flat.getHouse().getId())) {
                houseRepository.deleteById(flat.getHouse().getId());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "U do not own this flat");
        }
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

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }
}
