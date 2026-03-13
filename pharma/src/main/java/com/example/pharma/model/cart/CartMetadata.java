package com.example.pharma.model.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartMetadata {
    private Long cartId;
    private Long userId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
