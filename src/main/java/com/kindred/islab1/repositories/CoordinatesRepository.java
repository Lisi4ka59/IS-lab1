package com.kindred.islab1.repositories;

import com.kindred.islab1.entities.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Long> {
    List<Coordinates> findAllByOrderByIdAsc();
}
