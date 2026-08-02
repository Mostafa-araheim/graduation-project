import { Component, inject, signal, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgFor, NgIf, NgClass } from '@angular/common';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { PharmacyDto } from '../../../core/models/pharmacy.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgFor, NgIf, NgClass],
  template: `
    <!-- Mobile overlay backdrop -->
    <div
      *ngIf="isOpen()"
      class="fixed inset-0 bg-black/50 backdrop-blur-sm z-40 lg:hidden"
      (click)="closeSidebar()"
    ></div>

    <!-- Sidebar -->
    <aside
      class="fixed top-0 left-0 z-50 h-screen gradient-sidebar flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0"
      [ngClass]="{
        'translate-x-0': isOpen(),
        '-translate-x-full': !isOpen()
      }"
      style="width: 260px;"
    >
      <!-- ── Logo / Brand ──────────────────────────────── -->
      <div class="flex items-center gap-3 px-6 py-6 border-b border-white/10">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center shadow-lg">
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
          </svg>
        </div>
        <div>
          <h1 class="text-white font-heading font-bold text-lg tracking-tight">PharmaOwner</h1>
          <p class="text-slate-400 text-xs">Management Portal</p>
        </div>
      </div>

      <!-- ── Navigation Links ──────────────────────────── -->
      <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <a
          routerLink="/dashboard"
          routerLinkActive="active-nav-link"
          [routerLinkActiveOptions]="{ exact: true }"
          class="nav-link group"
        >
          <div class="nav-icon-wrapper">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
          </div>
          <span class="nav-text">Dashboard</span>
        </a>

        <a
          routerLink="/pharmacies"
          routerLinkActive="active-nav-link"
          class="nav-link group"
        >
          <div class="nav-icon-wrapper">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <span class="nav-text">Pharmacies</span>
        </a>

        <a
          routerLink="/analytics"
          routerLinkActive="active-nav-link"
          class="nav-link group"
        >
          <div class="nav-icon-wrapper">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <span class="nav-text">Analytics</span>
        </a>

        <a
          routerLink="/profile"
          routerLinkActive="active-nav-link"
          class="nav-link group"
        >
          <div class="nav-icon-wrapper">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <span class="nav-text">Profile</span>
        </a>
      </nav>

      <!-- ── Pharmacy Selector ─────────────────────────── -->
      <div class="px-4 py-3 border-t border-white/10" *ngIf="pharmacyService.pharmacies().length > 0">
        <label class="text-slate-400 text-xs font-medium mb-1.5 block uppercase tracking-wider">Active Pharmacy</label>
        <div class="relative">
          <select
            class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2.5 text-sm text-white
                   appearance-none cursor-pointer focus:outline-none focus:ring-2 focus:ring-primary-500/40
                   focus:border-primary-500/40 transition-all duration-200 hover:bg-white/10"
            [value]="pharmacyService.selectedPharmacy()?.pharmacy_id"
            (change)="onPharmacyChange($event)"
          >
            <option
              *ngFor="let pharmacy of pharmacyService.pharmacies()"
              [value]="pharmacy.pharmacy_id"
              class="bg-slate-800 text-white"
            >
              {{ pharmacy.name }}
            </option>
          </select>
          <svg class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </div>

      <!-- ── Bottom Branding ───────────────────────────── -->
      <div class="px-6 py-4 border-t border-white/10">
        <div class="flex items-center gap-2">
          <div class="w-2 h-2 rounded-full bg-accent-400 animate-pulse-soft"></div>
          <p class="text-slate-500 text-xs">Pharmacy Owner Panel</p>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .nav-link {
      @apply flex items-center gap-3 px-3 py-2.5 rounded-xl text-slate-300
             transition-all duration-200 cursor-pointer
             hover:bg-white/5 hover:text-white;
    }

    .nav-link.active-nav-link {
      @apply bg-primary-500/15 text-white;
    }

    .nav-link.active-nav-link .nav-icon-wrapper {
      @apply bg-primary-500 text-white shadow-lg shadow-primary-500/25;
    }

    .nav-icon-wrapper {
      @apply w-9 h-9 rounded-lg flex items-center justify-center
             bg-white/5 text-slate-400 transition-all duration-200
             group-hover:bg-white/10 group-hover:text-white;
    }

    .nav-text {
      @apply text-sm font-medium;
    }
  `]
})
export class SidebarComponent {
  readonly pharmacyService = inject(PharmacyService);

  /** Whether the sidebar is open on mobile */
  readonly isOpen = signal(false);

  toggle(): void {
    this.isOpen.update(v => !v);
  }

  closeSidebar(): void {
    this.isOpen.set(false);
  }

  onPharmacyChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const pharmacyId = Number(select.value);
    const pharmacy = this.pharmacyService.pharmacies().find(p => p.pharmacy_id === pharmacyId);
    if (pharmacy) {
      this.pharmacyService.selectPharmacy(pharmacy);
    }
  }
}
