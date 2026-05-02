package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PharmacyDashboardSummaryResponse(

        @JsonProperty("pharmacy_id")
        Long pharmacyId,

        @JsonProperty("total_products")
        Long totalProducts,

        @JsonProperty("out_of_stock_count")
        Long outOfStockCount,

        @JsonProperty("limited_supply_count")
        Long limitedSupplyCount
) {
}