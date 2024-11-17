package com.kindred.islab1.authentication.dto;

import com.kindred.islab1.entities.Role;
import lombok.Data;

import java.util.Set;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private String aboutUser;
    private Set<Role> role;
}
