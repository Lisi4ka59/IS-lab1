package com.kindred.islab1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role {
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)

    @JsonIgnore
    private Set<User> users;
    @Override
    public String toString() {
        return String.format("Role{id=%d, name='%s'}", id, name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
