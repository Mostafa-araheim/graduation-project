package com.example.pharma.model.entity.P2P;

public enum ListingStatus {

        AVAILABLE,    // Visible in search
        PENDING,      // User is in checkout/transaction
        SOLD,         // No longer available
        EXPIRED,      // Passed the expiry date
        CANCELLED     // Seller removed the listing
}
