import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { AnalyticsService } from '../../../core/services/analytics.service';
import { PharmacyDto } from '../../../core/models/pharmacy.model';
import { PharmacyDashboardSummaryResponse } from '../../../core/models/analytics.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EgpCurrencyPipe } from '../../../shared/pipes/currency-egp.pipe';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-pharmacy-detail',
  standalone: true,
  imports: [
    CommonModule,
    StatusBadgeComponent,
    MetricCardComponent,
    LoadingSpinnerComponent,
    EgpCurrencyPipe,
  ],
  template: `
    @if (loading()) {
      <app-loading-spinner message="Loading pharmacy details..." />
    } @else if (pharmacy()) {
      <div class="space-y-8">
        <!-- Back & Header -->
        <button
          (click)="goBack()"
          class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 transition-colors"
        >
          <svg
            class="w-4 h-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M15 19l-7-7 7-7"
            />
          </svg>
          Back to Pharmacies
        </button>

        <!-- Pharmacy Header Card -->
        <div
          class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
        >
          <div class="relative h-56">
            @if (pharmacy()!.image_url) {
              <img
                [src]="base + pharmacy()!.image_url"
                [alt]="pharmacy()!.name"
                class="w-full h-full object-cover"
              />
            } @else {
              <div
                class="w-full h-full bg-gradient-to-br from-blue-500 via-indigo-500 to-purple-600 flex items-center justify-center"
              >
                <span class="text-6xl text-white/80 font-bold font-['Outfit']">
                  {{ pharmacy()!.name.charAt(0).toUpperCase() }}
                </span>
              </div>
            }
            <div
              class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent"
            ></div>
            <div
              class="absolute bottom-4 left-6 right-6 flex items-end justify-between"
            >
              <div>
                <h1 class="text-2xl font-bold text-white font-['Outfit']">
                  {{ pharmacy()!.name }}
                </h1>
                <p class="text-white/80 text-sm mt-1">
                  {{ pharmacy()!.address }}
                </p>
              </div>
              <app-status-badge
                [status]="pharmacy()!.is_closed ? 'closed' : 'open'"
              />
            </div>
          </div>

          <div class="p-6">
            <div
              class="flex flex-wrap items-center gap-6 text-sm text-gray-600"
            >
              <!-- Rating -->
              <div class="flex items-center gap-2">
                <div class="flex">
                  @for (star of getStars(pharmacy()!.rating); track $index) {
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
                <span class="font-medium">{{
                  pharmacy()!.rating.toFixed(1)
                }}</span>
                <span class="text-gray-400"
                  >({{ pharmacy()!.review_count }} reviews)</span
                >
              </div>

              <div class="h-4 w-px bg-gray-200"></div>

              <!-- Hours -->
              <div class="flex items-center gap-1.5">
                <svg
                  class="w-4 h-4 text-gray-400"
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
                {{ pharmacy()!.opening_time }} - {{ pharmacy()!.closing_time }}
              </div>
            </div>

            <!-- Action Buttons -->
            <div
              class="flex flex-wrap gap-3 mt-6 pt-6 border-t border-gray-100"
            >
              <button
                (click)="editPharmacy()"
                class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-blue-50 text-blue-600
                             text-sm font-medium hover:bg-blue-100 transition-colors"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                  />
                </svg>
                Edit
              </button>
              <button
                (click)="viewInventory()"
                class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-50 text-emerald-600
                             text-sm font-medium hover:bg-emerald-100 transition-colors"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
                  />
                </svg>
                View Inventory
              </button>
              <button
                (click)="viewOrders()"
                class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-purple-50 text-purple-600
                             text-sm font-medium hover:bg-purple-100 transition-colors"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                  />
                </svg>
                View Orders
              </button>
              <button
                (click)="viewReviews()"
                class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-amber-50 text-amber-600
                             text-sm font-medium hover:bg-amber-100 transition-colors"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"
                  />
                </svg>
                View Reviews
              </button>
              <button
                (click)="deletePharmacy()"
                class="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-rose-50 text-rose-600
                             text-sm font-medium hover:bg-rose-100 transition-colors ml-auto"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                  />
                </svg>
                Delete
              </button>
            </div>
          </div>
        </div>

        <!-- Dashboard Summary Metrics -->
        @if (summary()) {
          <div>
            <h2
              class="text-lg font-semibold text-gray-900 font-['Outfit'] mb-4"
            >
              Pharmacy Overview
            </h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <app-metric-card
                label="Total Products"
                [value]="summary()!.total_products"
                icon="📦"
                color="blue"
              />
              <app-metric-card
                label="Total Orders"
                [value]="summary()!.total_orders"
                icon="🛒"
                color="purple"
              />
              <app-metric-card
                label="Revenue"
                [value]="summary()!.total_revenue | egp"
                icon="💰"
                color="emerald"
              />
              <app-metric-card
                label="Average Rating"
                [value]="summary()!.average_rating.toFixed(1) + ' ★'"
                icon="⭐"
                color="amber"
                [subtitle]="summary()!.total_reviews + ' reviews'"
              />
            </div>
          </div>
        }
      </div>
    }
  `,
})
export class PharmacyDetailComponent implements OnInit {
  private pharmacyService = inject(PharmacyService);
  private analyticsService = inject(AnalyticsService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  pharmacy = signal<PharmacyDto | null>(null);
  summary = signal<PharmacyDashboardSummaryResponse | null>(null);
  loading = signal(true);
  base = environment.base;
  ngOnInit(): void {
    const id = +this.route.snapshot.paramMap.get('id')!;
    this.loadPharmacy(id);
    this.loadSummary(id);
  }

  private loadPharmacy(id: number): void {
    this.pharmacyService.getPharmacyById(id).subscribe({
      next: (pharmacy) => {
        this.pharmacy.set(pharmacy);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.router.navigate(['/pharmacies']);
      },
    });
  }

  private loadSummary(id: number): void {
    this.analyticsService.getPharmacyDashboardSummary(id).subscribe({
      next: (summary) => this.summary.set(summary),
    });
  }

  editPharmacy(): void {
    this.router.navigate(['/pharmacies', this.pharmacy()!.pharmacy_id, 'edit']);
  }

  viewInventory(): void {
    this.router.navigate(['/inventory', this.pharmacy()!.pharmacy_id]);
  }

  viewOrders(): void {
    this.router.navigate(['/orders', this.pharmacy()!.pharmacy_id]);
  }

  viewReviews(): void {
    this.router.navigate(['/reviews', this.pharmacy()!.pharmacy_id]);
  }

  deletePharmacy(): void {
    if (
      window.confirm(
        `Are you sure you want to delete "${this.pharmacy()!.name}"? This action cannot be undone.`,
      )
    ) {
      this.pharmacyService
        .deletePharmacy(this.pharmacy()!.pharmacy_id)
        .subscribe({
          next: () => this.router.navigate(['/pharmacies']),
        });
    }
  }

  goBack(): void {
    this.router.navigate(['/pharmacies']);
  }

  getStars(rating: number): boolean[] {
    return Array.from({ length: 5 }, (_, i) => i < Math.round(rating));
  }
}
