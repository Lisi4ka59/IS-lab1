package com.kindred.islab1.repositories;


import com.kindred.islab1.entities.Role;
import com.kindred.islab1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Set<Role> findByUsers(Set<User> users);
}