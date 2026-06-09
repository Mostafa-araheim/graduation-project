package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record PharmacyDashboardSummaryResponse(

        @JsonProperty("pharmacy_id")
        Long pharmacyId,

        @JsonProperty("total_products")
        Long totalProducts,

        @JsonProperty("out_of_stock_count")
        Long outOfStockCount,

        @JsonProperty("limited_supply_count")
        Long limitedSupplyCount,

        @JsonProperty("total_orders")
        Long totalOrders,

        @JsonProperty("pending_orders")
        Long pendingOrders,

        @JsonProperty("total_revenue")
        BigDecimal totalRevenue,

        @JsonProperty("average_rating")
        Double averageRating,

        @JsonProperty("total_reviews")
        Long totalReviews
) {
}