import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ProfileService } from '../../../core/services/profile.service';
import { OwnerProfileDto } from '../../../core/models/profile.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-profile-view',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, DatePipe],
  template: `
    @if (loading()) {
      <app-loading-spinner message="Loading profile..." />
    } @else if (profile()) {
      <div class="max-w-2xl mx-auto">
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <!-- Gradient Header -->
          <div class="relative h-40 bg-gradient-to-br from-blue-600 via-indigo-600 to-purple-700">
            <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMTAiIGN5PSIxMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjEpIi8+PC9zdmc+')] opacity-30"></div>
          </div>

          <!-- Avatar -->
          <div class="relative -mt-16 flex justify-center">
            @if (profile()!.image_url) {
              <img [src]="profile()!.image_url" [alt]="profile()!.name"
                   class="w-28 h-28 rounded-2xl border-4 border-white shadow-lg object-cover">
            } @else {
              <div class="w-28 h-28 rounded-2xl border-4 border-white shadow-lg bg-gradient-to-br from-blue-500 to-indigo-600
                          flex items-center justify-center">
                <span class="text-3xl font-bold text-white font-['Outfit']">
                  {{ getInitials(profile()!.name) }}
                </span>
              </div>
            }
          </div>

          <!-- Info -->
          <div class="text-center px-6 pt-4 pb-2">
            <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">{{ profile()!.name }}</h1>
            <p class="text-sm text-gray-500 mt-1">Pharmacy Owner</p>
          </div>

          <!-- Details -->
          <div class="px-6 pb-8 pt-4 space-y-4">
            <div class="divide-y divide-gray-100 rounded-xl border border-gray-100 overflow-hidden">
              <!-- Email -->
              <div class="flex items-center gap-4 px-5 py-4">
                <div class="flex-shrink-0 w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center">
                  <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
                  </svg>
                </div>
                <div>
                  <p class="text-xs text-gray-400 font-medium">Email</p>
                  <p class="text-sm text-gray-900">{{ profile()!.email }}</p>
                </div>
              </div>

              <!-- Phone -->
              <div class="flex items-center gap-4 px-5 py-4">
                <div class="flex-shrink-0 w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center">
                  <svg class="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/>
                  </svg>
                </div>
                <div>
                  <p class="text-xs text-gray-400 font-medium">Phone</p>
                  <p class="text-sm text-gray-900">{{ profile()!.phone || 'Not provided' }}</p>
                </div>
              </div>

              <!-- Member Since -->
              <div class="flex items-center gap-4 px-5 py-4">
                <div class="flex-shrink-0 w-10 h-10 rounded-xl bg-purple-50 flex items-center justify-center">
                  <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                  </svg>
                </div>
                <div>
                  <p class="text-xs text-gray-400 font-medium">Member Since</p>
                  <p class="text-sm text-gray-900">{{ profile()!.member_since | date:'MMMM d, yyyy' }}</p>
                </div>
              </div>

              <!-- Total Pharmacies -->
              <div class="flex items-center gap-4 px-5 py-4">
                <div class="flex-shrink-0 w-10 h-10 rounded-xl bg-amber-50 flex items-center justify-center">
                  <svg class="w-5 h-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                  </svg>
                </div>
                <div>
                  <p class="text-xs text-gray-400 font-medium">Total Pharmacies</p>
                  <p class="text-sm text-gray-900">{{ profile()!.total_pharmacies }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    }
  `
})
export class ProfileViewComponent implements OnInit {
  private profileService = inject(ProfileService);

  profile = signal<OwnerProfileDto | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.profileService.getOwnerProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
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
