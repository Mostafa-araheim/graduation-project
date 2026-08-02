import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { PharmacyReviewDetailDto } from '../models/review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getPharmacyReviews(
    pharmacyId: number,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<PharmacyReviewDetailDto>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<ApiResponse<PageResponse<PharmacyReviewDetailDto>>>(
      `${this.apiUrl}/pharmacies/${pharmacyId}/reviews`, { params }
    ).pipe(map(res => res.data!));
  }
}
