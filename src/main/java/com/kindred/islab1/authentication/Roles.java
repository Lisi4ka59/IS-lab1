package com.kindred.islab1.authentication;


import lombok.Getter;

@Getter
public enum Roles {
    USER("USER"),
    ADMIN("ADMIN"),
    OWNER("OWNER");

    private final String role;

    Roles(String role) {
        this.role = role;
    }

}
