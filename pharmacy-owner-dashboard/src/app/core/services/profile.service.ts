import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { OwnerProfileDto } from '../models/profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  readonly profile = signal<OwnerProfileDto | null>(null);

  getOwnerProfile(): Observable<OwnerProfileDto> {
    return this.http.get<ApiResponse<OwnerProfileDto>>(
      `${this.apiUrl}/pharmacies/owner/profile`
    ).pipe(
      map(res => res.data!),
      tap(data => this.profile.set(data))
    );
  }
}
