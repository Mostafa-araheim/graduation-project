package com.example.pharma.model.entity.order;

public enum PaymentStatus {
    INITIATED,
    REQUIRES_ACTION,
    PENDING_CASH,
    SUCCEEDED,
    FAILED,
    CANCELED
}