package com.example.pharma.dto.pharmacy.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SalesAnalyticsResponse(
        @JsonProperty("total_revenue")
        BigDecimal totalRevenue,

        @JsonProperty("total_orders")
        Long totalOrders,

        @JsonProperty("average_order_value")
        BigDecimal averageOrderValue,

        @JsonProperty("sales_over_time")
        List<DailySalesDto> salesOverTime,

        @JsonProperty("best_sellers")
        List<BestSellerProductDto> bestSellers,

        @JsonProperty("status_distribution")
        Map<String, Long> statusDistribution
) {}
