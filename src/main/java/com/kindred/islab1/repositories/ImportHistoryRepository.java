package com.kindred.islab1.repositories;

import com.kindred.islab1.entities.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    ArrayList<ImportHistory> findAllByOwnerIdOrderByCreationDateDesc(long id);
    ArrayList<ImportHistory> findAllByOrderByCreationDateDesc();
}
