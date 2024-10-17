package com.kindred.islab1.repositories;

import com.kindred.islab1.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {}