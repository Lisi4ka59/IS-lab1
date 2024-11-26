package com.kindred.islab1.repositories;


import com.kindred.islab1.authentication.Roles;
import com.kindred.islab1.entities.Role;
import com.kindred.islab1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Roles name);
    Set<Role> findByUsers(Set<User> users);
}