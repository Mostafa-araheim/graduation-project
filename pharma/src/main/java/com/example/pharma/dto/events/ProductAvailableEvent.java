package com.example.pharma.dto.events;

public record ProductAvailableEvent(
        Long productId,
        String productName,
        String producerName,
        String source // "PHARMACY" or "P2P"
)
{}
