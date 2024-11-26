package com.kindred.islab1.services;

import com.kindred.islab1.authentication.Roles;
import com.kindred.islab1.entities.Role;
import com.kindred.islab1.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Role ensureRoleExists(Roles roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role(roleName);
            return roleRepository.save(newRole);
        });
    }
}
