import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ReviewService } from '../../../core/services/review.service';
import { PharmacyReviewDetailDto } from '../../../core/models/review.model';
import { PageResponse } from '../../../core/models/api-response.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-reviews-list',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, DatePipe],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div>
        <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">Reviews</h1>
        <p class="text-sm text-gray-500 mt-1">See what customers are saying</p>
      </div>

      @if (loading()) {
        <app-loading-spinner message="Loading reviews..." />
      }

      <!-- Reviews Grid -->
      @if (!loading() && reviews().length > 0) {
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          @for (review of reviews(); track review.review_id) {
            <div class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm
                        hover:shadow-md transition-all duration-300">
              <div class="flex items-start gap-4">
                <!-- Avatar -->
                <div class="flex-shrink-0 w-11 h-11 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600
                            flex items-center justify-center">
                  <span class="text-sm font-bold text-white">
                    {{ getInitials(review.customer_name) }}
                  </span>
                </div>

                <div class="flex-1 min-w-0">
                  <!-- Name & Date -->
                  <div class="flex items-center justify-between gap-2">
                    <h3 class="text-sm font-semibold text-gray-900 truncate">{{ review.customer_name }}</h3>
                    <time class="text-xs text-gray-400 flex-shrink-0">
                      {{ review.created_at | date:'MMM d, yyyy' }}
                    </time>
                  </div>

                  <!-- Comment -->
                  <p class="text-sm text-gray-600 mt-2 leading-relaxed">{{ review.comment }}</p>
                </div>
              </div>
            </div>
          }
        </div>

        <!-- Pagination -->
        @if (pageData()) {
          <div class="flex items-center justify-center gap-3 pt-4">
            <button (click)="goToPage(currentPage() - 1)" [disabled]="currentPage() === 0"
                    class="px-4 py-2 rounded-xl border border-gray-200 text-sm text-gray-600
                           hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
              Previous
            </button>
            <span class="text-sm font-medium text-gray-700">
              Page {{ currentPage() + 1 }} of {{ pageData()!.totalPages }}
            </span>
            <button (click)="goToPage(currentPage() + 1)" [disabled]="pageData()!.last"
                    class="px-4 py-2 rounded-xl border border-gray-200 text-sm text-gray-600
                           hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
              Next
            </button>
          </div>
        }
      }

      <!-- Empty -->
      @if (!loading() && reviews().length === 0) {
        <div class="flex flex-col items-center justify-center py-20 text-center">
          <div class="w-20 h-20 rounded-2xl bg-amber-50 flex items-center justify-center mb-4">
            <svg class="w-10 h-10 text-amber-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">No reviews yet</h3>
          <p class="text-sm text-gray-500 mt-1">Customer reviews will appear here once they start leaving feedback.</p>
        </div>
      }
    </div>
  `
})
export class ReviewsListComponent implements OnInit {
  private reviewService = inject(ReviewService);
  private route = inject(ActivatedRoute);

  pharmacyId = 0;
  reviews = signal<PharmacyReviewDetailDto[]>([]);
  pageData = signal<PageResponse<PharmacyReviewDetailDto> | null>(null);
  loading = signal(true);
  currentPage = signal(0);

  ngOnInit(): void {
    this.pharmacyId = +this.route.snapshot.paramMap.get('pharmacyId')!;
    this.loadReviews();
  }

  loadReviews(): void {
    this.loading.set(true);
    this.reviewService.getPharmacyReviews(this.pharmacyId, this.currentPage(), 10).subscribe({
      next: (page) => {
        this.reviews.set(page.content);
        this.pageData.set(page);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadReviews();
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map(n => n.charAt(0))
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }
}
