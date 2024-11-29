package com.kindred.islab1.repositories;

import com.kindred.islab1.authentication.RequestStatus;
import com.kindred.islab1.entities.AdminRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface AdminRequestRepository extends JpaRepository<AdminRequest, Long> {
    boolean existsAdminRequestByUsername(String username);
    Optional<AdminRequest> findAdminRequestByUsernameAndStatus(String username, RequestStatus status);
    Optional<ArrayList<AdminRequest>> findAllByStatus(RequestStatus status);
    Optional<AdminRequest> findAdminRequestByUsernameAndStatusOrderByIdDesc(String username, RequestStatus status);
}
