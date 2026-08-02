import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PageResponse,
  PaginationParams,
} from '../models/api-response.model';
import {
  PharmacyProductDto,
  AddPharmacyProductRequest,
  UpdatePharmacyProductRequest,
  ProductResponse,
  InventoryFilterParams,
  ProductSearchParams,
} from '../models/inventory.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getInventory(
    pharmacyId: number,
    filters?: InventoryFilterParams,
    pagination?: PaginationParams,
  ): Observable<PageResponse<PharmacyProductDto>> {
    let params = new HttpParams();
    if (filters?.productName)
      params = params.set('productName', filters.productName);
    if (filters?.availabilityStatus)
      params = params.set('availabilityStatus', filters.availabilityStatus);
    if (filters?.categoryName)
      params = params.set('categoryName', filters.categoryName);
    if (filters?.minPrice !== undefined)
      params = params.set('minPrice', filters.minPrice);
    if (filters?.maxPrice !== undefined)
      params = params.set('maxPrice', filters.maxPrice);
    if (pagination?.page !== undefined)
      params = params.set('page', pagination.page);
    if (pagination?.size !== undefined)
      params = params.set('size', pagination.size);
    if (pagination?.sort) params = params.set('sort', pagination.sort);

    return this.http
      .get<
        ApiResponse<PageResponse<PharmacyProductDto>>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}/products`, { params })
      .pipe(map((res) => res.data!));
  }

  getProductDetail(
    pharmacyId: number,
    productId: number,
  ): Observable<PharmacyProductDto> {
    return this.http
      .get<
        ApiResponse<PharmacyProductDto>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}/products/${productId}`)
      .pipe(map((res) => res.data!));
  }

  addProduct(
    pharmacyId: number,
    request: AddPharmacyProductRequest,
  ): Observable<void> {
    return this.http
      .post<
        ApiResponse<void>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}/products`, request)
      .pipe(map(() => void 0));
  }

  updateProduct(
    pharmacyId: number,
    productId: number,
    request: UpdatePharmacyProductRequest,
  ): Observable<void> {
    return this.http
      .patch<
        ApiResponse<void>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}/products/${productId}`, request)
      .pipe(map(() => void 0));
  }

  deleteProduct(pharmacyId: number, productId: number): Observable<void> {
    return this.http
      .delete<
        ApiResponse<void>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}/products/${productId}`)
      .pipe(map(() => void 0));
  }

  searchGlobalProducts(
    filters?: ProductSearchParams,
    pagination?: PaginationParams,
  ): Observable<PageResponse<ProductResponse>> {
    let params = new HttpParams();
    if (filters?.productName)
      params = params.set('productName', filters.productName);
    if (filters?.categoryName)
      params = params.set('categoryName', filters.categoryName);
    if (filters?.dosageForm)
      params = params.set('dosageForm', filters.dosageForm);
    if (pagination?.page !== undefined)
      params = params.set('page', pagination.page);
    if (pagination?.size !== undefined)
      params = params.set('size', pagination.size);
    if (pagination?.sort) params = params.set('sort', pagination.sort);

    return this.http
      .get<
        ApiResponse<PageResponse<ProductResponse>>
      >(`${this.apiUrl}/pharmacies/owner/products/search`, { params })
      .pipe(map((res) => res.data!));
  }

  getUniqueProducts(): Observable<any> {
    return this.http.get(this.apiUrl + '/Products');
  }
}
