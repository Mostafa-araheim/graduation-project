export interface OwnerDashboardSummaryResponse {
  total_pharmacies: number;
  total_products: number;
  out_of_stock_count: number;
  limited_supply_count: number;
  total_orders: number;
  total_revenue: number;
}

export interface PharmacyDashboardSummaryResponse {
  pharmacy_id: number;
  total_products: number;
  out_of_stock_count: number;
  limited_supply_count: number;
  total_orders: number;
  pending_orders: number;
  total_revenue: number;
  average_rating: number;
  total_reviews: number;
}

export interface SalesAnalyticsResponse {
  total_revenue: number;
  total_orders: number;
  average_order_value: number;
  sales_over_time: SalesOverTime[];
  best_sellers: BestSeller[];
  status_distribution: StatusDistribution;
}

export interface SalesOverTime {
  date: string;
  revenue: number;
  orderCount: number;
}

export interface BestSeller {
  productId: number;
  productName: string;
  quantitySold: number;
  totalRevenue: number;
}

export interface StatusDistribution {
  [key: string]: number;
}
