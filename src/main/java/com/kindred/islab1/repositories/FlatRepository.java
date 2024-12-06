package com.kindred.islab1.repositories;

import com.kindred.islab1.entities.Flat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlatRepository extends JpaRepository<Flat, Long> {
    boolean existsByCoordinatesId(Long id);
    boolean existsByHouseId(Long id);
    Optional<Flat> findFirstByHouse_Id(long id);

}