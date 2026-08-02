import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AnalyticsPeriod } from '../models/enums.model';
import {
  OwnerDashboardSummaryResponse,
  PharmacyDashboardSummaryResponse,
  SalesAnalyticsResponse
} from '../models/analytics.model';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  readonly ownerSummary = signal<OwnerDashboardSummaryResponse | null>(null);
  readonly pharmacySummary = signal<PharmacyDashboardSummaryResponse | null>(null);
  readonly salesAnalytics = signal<SalesAnalyticsResponse | null>(null);

  getOwnerDashboardSummary(): Observable<OwnerDashboardSummaryResponse> {
    return this.http.get<ApiResponse<OwnerDashboardSummaryResponse>>(
      `${this.apiUrl}/pharmacies/owner/dashboard-summary`
    ).pipe(
      map(res => res.data!),
      tap(data => this.ownerSummary.set(data))
    );
  }

  getPharmacyDashboardSummary(pharmacyId: number): Observable<PharmacyDashboardSummaryResponse> {
    return this.http.get<ApiResponse<PharmacyDashboardSummaryResponse>>(
      `${this.apiUrl}/pharmacies/${pharmacyId}/dashboard-summary`
    ).pipe(
      map(res => res.data!),
      tap(data => this.pharmacySummary.set(data))
    );
  }

  getOwnerSalesAnalytics(period: AnalyticsPeriod = 'month'): Observable<SalesAnalyticsResponse> {
    const params = new HttpParams().set('period', period);
    return this.http.get<ApiResponse<SalesAnalyticsResponse>>(
      `${this.apiUrl}/pharmacies/owner/sales-analytics`, { params }
    ).pipe(
      map(res => res.data!),
      tap(data => this.salesAnalytics.set(data))
    );
  }

  getPharmacySalesAnalytics(pharmacyId: number, period: AnalyticsPeriod = 'month'): Observable<SalesAnalyticsResponse> {
    const params = new HttpParams().set('period', period);
    return this.http.get<ApiResponse<SalesAnalyticsResponse>>(
      `${this.apiUrl}/pharmacies/${pharmacyId}/sales-analytics`, { params }
    ).pipe(
      map(res => res.data!),
      tap(data => this.salesAnalytics.set(data))
    );
  }
}
