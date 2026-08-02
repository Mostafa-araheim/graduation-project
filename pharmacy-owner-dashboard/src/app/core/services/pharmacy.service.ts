import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  PageResponse,
  PaginationParams,
} from '../models/api-response.model';
import {
  PharmacyDto,
  CreatePharmacyRequest,
  UpdatePharmacyRequest,
} from '../models/pharmacy.model';

@Injectable({ providedIn: 'root' })
export class PharmacyService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // ── State ─────────────────────────────────────────────
  readonly pharmacies = signal<PharmacyDto[]>([]);
  readonly selectedPharmacy = signal<PharmacyDto | null>(null);
  readonly loading = signal<boolean>(false);

  // ── API Calls ─────────────────────────────────────────

  getOwnerPharmacies(
    params?: PaginationParams,
  ): Observable<PageResponse<PharmacyDto>> {
    let httpParams = new HttpParams();
    if (params?.page !== undefined)
      httpParams = httpParams.set('page', params.page);
    if (params?.size !== undefined)
      httpParams = httpParams.set('size', params.size);
    if (params?.sort) httpParams = httpParams.set('sort', params.sort);

    this.loading.set(true);
    return this.http
      .get<
        ApiResponse<PageResponse<PharmacyDto>>
      >(`${this.apiUrl}/pharmacies/owner`, { params: httpParams })
      .pipe(
        map((res) => res.data!),
        tap((page) => {
          this.pharmacies.set(page.content);
          if (!this.selectedPharmacy() && page.content.length > 0) {
            this.selectedPharmacy.set(page.content[0]);
          }
          this.loading.set(false);
        }),
      );
  }

  getPharmacyById(pharmacyId: number): Observable<PharmacyDto> {
    return this.http
      .get<
        ApiResponse<PharmacyDto>
      >(`${this.apiUrl}/pharmacies/owner/${pharmacyId}`)
      .pipe(map((res) => res.data!));
  }

  createPharmacy(formData: FormData): Observable<PharmacyDto> {
    return this.http
      .post<ApiResponse<PharmacyDto>>(`${this.apiUrl}/pharmacies`, formData)
      .pipe(
        map((res) => res.data!),
        tap(() => this.getOwnerPharmacies().subscribe()),
      );
  }

  updatePharmacy(
    pharmacyId: number,
    request: UpdatePharmacyRequest,
  ): Observable<PharmacyDto> {
    console.log('here');
    return this.http
      .patch<
        ApiResponse<PharmacyDto>
      >(`${this.apiUrl}/pharmacies/${pharmacyId}`, request)
      .pipe(
        map((res) => res.data!),
        tap(() => this.getOwnerPharmacies().subscribe()),
      );
  }

  deletePharmacy(pharmacyId: number): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.apiUrl}/pharmacies/${pharmacyId}`)
      .pipe(
        map(() => void 0),
        tap(() => {
          this.pharmacies.update((list) =>
            list.filter((p) => p.pharmacy_id !== pharmacyId),
          );
          if (this.selectedPharmacy()?.pharmacy_id === pharmacyId) {
            this.selectedPharmacy.set(this.pharmacies()[0] || null);
          }
        }),
      );
  }

  selectPharmacy(pharmacy: PharmacyDto): void {
    this.selectedPharmacy.set(pharmacy);
  }
}
