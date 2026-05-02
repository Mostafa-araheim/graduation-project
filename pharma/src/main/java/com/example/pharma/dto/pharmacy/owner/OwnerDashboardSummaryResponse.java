package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OwnerDashboardSummaryResponse(

        @JsonProperty("total_pharmacies")
        Long totalPharmacies,

        @JsonProperty("total_products")
        Long totalProducts,

        @JsonProperty("out_of_stock_count")
        Long outOfStockCount,

        @JsonProperty("limited_supply_count")
        Long limitedSupplyCount
) {
}