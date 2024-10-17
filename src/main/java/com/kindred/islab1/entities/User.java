package com.kindred.islab1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_info")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles;
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', password='%s'}", id, username, email, password);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password);
    }

    public void addRoles(Role role) {
        roles.add(role);
    }
    public void removeRoles(Role role) {
        roles.remove(role);
    }
}
