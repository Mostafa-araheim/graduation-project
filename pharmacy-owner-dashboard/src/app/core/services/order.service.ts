import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse, PaginationParams } from '../models/api-response.model';
import { OwnerOrderResponse } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getPharmacyOrders(
    pharmacyId: number,
    pagination?: PaginationParams
  ): Observable<PageResponse<OwnerOrderResponse>> {
    let params = new HttpParams();
    if (pagination?.page !== undefined) params = params.set('page', pagination.page);
    if (pagination?.size !== undefined) params = params.set('size', pagination.size);
    if (pagination?.sort) params = params.set('sort', pagination.sort);

    return this.http.get<ApiResponse<PageResponse<OwnerOrderResponse>>>(
      `${this.apiUrl}/pharmacies/${pharmacyId}/orders`, { params }
    ).pipe(map(res => res.data!));
  }
}
