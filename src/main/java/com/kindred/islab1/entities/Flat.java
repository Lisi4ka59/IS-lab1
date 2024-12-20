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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Coordinates coordinates;

    @NotNull
    private LocalDateTime creationDate;

    @Positive
    private Double area;

    @Positive
    @Max(777301647)
    private Double price;

    private Boolean balcony;

    @Positive
    private Double timeToMetroOnFoot;

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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private House house;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    @NotNull
    private long ownerId;

}

