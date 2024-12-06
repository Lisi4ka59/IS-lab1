package com.kindred.islab1.entities;

import com.kindred.islab1.authentication.ImportStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ImportStatus status;

    private long createdFlats;

    private long createdHouses;

    private long createdCoordinates;

    private String username;

    @NotNull
    private long ownerId;

    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}
