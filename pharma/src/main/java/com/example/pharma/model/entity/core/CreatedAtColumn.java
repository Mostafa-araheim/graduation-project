package com.example.pharma.model.entity.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDateTime;

@Embeddable
@Getter
public class CreatedAtColumn {

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime value;
}