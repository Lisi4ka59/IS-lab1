package com.kindred.islab1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Coordinates coordinates;

    @NotNull
    private LocalDateTime creationDate;

    @Positive
    private double area;

    @Positive
    @Max(777301647)
    private double price;

    private boolean balcony;

    @Positive
    private double timeToMetroOnFoot;

    @Positive
    private Long numberOfRooms;

    @NotNull
    private Boolean isNew;

    @Enumerated(EnumType.STRING)
    private Furnish furnish;

    @NotNull
    @Enumerated(EnumType.STRING)
    private View view;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private House house;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

}

