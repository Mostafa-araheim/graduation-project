import { Component, inject, output, computed } from '@angular/core';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { ProfileService } from '../../../core/services/profile.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [NgIf],
  template: `
    <header class="h-16 bg-white border-b border-gray-100 px-6 flex items-center justify-between sticky top-0 z-30">
      <!-- Left: Hamburger + Page Title -->
      <div class="flex items-center gap-4">
        <!-- Hamburger (mobile only) -->
        <button
          class="lg:hidden p-2 rounded-lg text-gray-500 hover:bg-gray-100 hover:text-gray-700 transition-colors duration-200"
          (click)="sidebarToggle.emit()"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <!-- Breadcrumb / Page Title -->
        <div>
          <h2 class="font-heading font-semibold text-gray-900 text-lg">Welcome back</h2>
          <p class="text-xs text-gray-400" *ngIf="userEmail()">{{ userEmail() }}</p>
        </div>
      </div>

      <!-- Right: Actions -->
      <div class="flex items-center gap-3">
        <!-- Notification Bell -->
        <button class="relative p-2.5 rounded-xl text-gray-400 hover:bg-gray-50 hover:text-gray-600 transition-all duration-200">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <!-- Notification dot -->
          <span class="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full ring-2 ring-white"></span>
        </button>

        <!-- Divider -->
        <div class="w-px h-8 bg-gray-200"></div>

        <!-- User Avatar -->
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-full bg-gradient-to-br from-primary-500 to-primary-700 flex items-center justify-center text-white text-sm font-semibold shadow-md">
            {{ userInitials() }}
          </div>
          <div class="hidden sm:block">
            <p class="text-sm font-medium text-gray-700 leading-tight">{{ profileName() || 'Owner' }}</p>
            <p class="text-xs text-gray-400 leading-tight">Pharmacy Owner</p>
          </div>
        </div>

        <!-- Logout -->
        <button
          class="p-2.5 rounded-xl text-gray-400 hover:bg-red-50 hover:text-red-500 transition-all duration-200"
          (click)="onLogout()"
          title="Logout"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
              d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
        </button>
      </div>
    </header>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private profileService = inject(ProfileService);

  /** Emit when the hamburger is clicked to toggle the sidebar */
  sidebarToggle = output<void>();

  readonly userEmail = this.authService.userEmail;

  readonly profileName = computed(() => this.profileService.profile()?.name ?? null);

  readonly userInitials = computed(() => {
    const name = this.profileService.profile()?.name;
    if (name) {
      const parts = name.split(' ').filter(Boolean);
      return parts.length >= 2
        ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
        : (parts[0]?.[0] ?? 'O').toUpperCase();
    }
    const email = this.authService.userEmail();
    return email ? email[0].toUpperCase() : 'O';
  });

  onLogout(): void {
    this.authService.logout();
  }
}
