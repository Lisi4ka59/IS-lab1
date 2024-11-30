package com.kindred.islab1.authentication.dto;

import com.kindred.islab1.entities.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String aboutUser;
    private Set<Role> role;
}
