import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { PharmacyDto } from '../../../core/models/pharmacy.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-pharmacy-list',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, StatusBadgeComponent],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div
        class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">
            My Pharmacies
          </h1>
          <p class="text-sm text-gray-500 mt-1">
            Manage all your pharmacy locations
          </p>
        </div>
        <button
          (click)="navigateToNew()"
          class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gradient-to-r from-blue-600 to-blue-500
                       text-white font-medium text-sm shadow-lg shadow-blue-500/25 hover:shadow-blue-500/40
                       transition-all duration-300 hover:-translate-y-0.5"
        >
          <svg
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          Add New Pharmacy
        </button>
      </div>

      <!-- Loading -->
      @if (loading()) {
        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          @for (i of [1, 2, 3]; track i) {
            <div
              class="rounded-2xl bg-white border border-gray-100 overflow-hidden animate-pulse"
            >
              <div class="h-48 bg-gray-200"></div>
              <div class="p-5 space-y-3">
                <div class="h-5 bg-gray-200 rounded w-3/4"></div>
                <div class="h-4 bg-gray-200 rounded w-1/2"></div>
                <div class="h-4 bg-gray-200 rounded w-full"></div>
              </div>
            </div>
          }
        </div>
      }

      <!-- Empty State -->
      @if (!loading() && pharmacies().length === 0) {
        <div
          class="flex flex-col items-center justify-center py-20 text-center"
        >
          <div
            class="w-20 h-20 rounded-2xl bg-blue-50 flex items-center justify-center mb-4"
          >
            <svg
              class="w-10 h-10 text-blue-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="1.5"
                d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
              />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">No pharmacies yet</h3>
          <p class="text-sm text-gray-500 mt-1 max-w-sm">
            Get started by adding your first pharmacy location.
          </p>
          <button
            (click)="navigateToNew()"
            class="mt-4 px-5 py-2.5 rounded-xl bg-blue-600 text-white text-sm font-medium
                         hover:bg-blue-700 transition-colors"
          >
            Add Your First Pharmacy
          </button>
        </div>
      }

      <!-- Pharmacy Grid -->
      @if (!loading() && pharmacies().length > 0) {
        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          @for (pharmacy of pharmacies(); track pharmacy.pharmacy_id) {
            <div
              (click)="navigateToDetail(pharmacy.pharmacy_id)"
              class="group rounded-2xl bg-white border border-gray-100 overflow-hidden shadow-sm
                        hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer"
            >
              <!-- Image -->
              <div class="relative h-48 overflow-hidden">
                @if (pharmacy.image_url) {
                  <img
                    [src]="base + pharmacy.image_url"
                    [alt]="pharmacy.name"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                } @else {
                  <div
                    class="w-full h-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center"
                  >
                    <span
                      class="text-4xl text-white/80 font-bold font-['Outfit']"
                    >
                      {{ pharmacy.name.charAt(0).toUpperCase() }}
                    </span>
                  </div>
                }
                <!-- Status Badge Overlay -->
                <div class="absolute top-3 right-3">
                  <app-status-badge
                    [status]="pharmacy.is_closed ? 'closed' : 'open'"
                  />
                </div>
              </div>

              <!-- Content -->
              <div class="p-5 space-y-3">
                <h3
                  class="text-lg font-semibold text-gray-900 group-hover:text-blue-600 transition-colors"
                >
                  {{ pharmacy.name }}
                </h3>

                <!-- Rating -->
                <div class="flex items-center gap-2">
                  <div class="flex items-center">
                    @for (star of getStars(pharmacy.rating); track $index) {
                      <svg
                        class="w-4 h-4"
                        [class]="star ? 'text-amber-400' : 'text-gray-200'"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
                        />
                      </svg>
                    }
                  </div>
                  <span class="text-sm font-medium text-gray-700">{{
                    pharmacy.rating.toFixed(1)
                  }}</span>
                  <span class="text-xs text-gray-400"
                    >({{ pharmacy.review_count }} reviews)</span
                  >
                </div>

                <!-- Address -->
                <div class="flex items-start gap-2 text-sm text-gray-500">
                  <svg
                    class="w-4 h-4 mt-0.5 flex-shrink-0"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                    />
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                  </svg>
                  <span class="line-clamp-2">{{ pharmacy.address }}</span>
                </div>

                <!-- Hours -->
                <div class="flex items-center gap-2 text-xs text-gray-400">
                  <svg
                    class="w-3.5 h-3.5"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                  {{ pharmacy.opening_time }} - {{ pharmacy.closing_time }}
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
})
export class PharmacyListComponent implements OnInit {
  private pharmacyService = inject(PharmacyService);
  private router = inject(Router);
  base = environment.base;
  pharmacies = this.pharmacyService.pharmacies;
  loading = this.pharmacyService.loading;

  ngOnInit(): void {
    this.pharmacyService.getOwnerPharmacies().subscribe();
  }

  navigateToNew(): void {
    this.router.navigate(['/pharmacies/new']);
  }

  navigateToDetail(id: number): void {
    this.router.navigate(['/pharmacies', id]);
  }

  getStars(rating: number): boolean[] {
    return Array.from({ length: 5 }, (_, i) => i < Math.round(rating));
  }
}
