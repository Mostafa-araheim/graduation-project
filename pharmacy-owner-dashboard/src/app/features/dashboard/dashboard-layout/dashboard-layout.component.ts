import { Component, signal, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { HeaderComponent } from '../../../shared/components/header/header.component';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <!-- Sidebar -->
    <app-sidebar #sidebar />

    <!-- Main Content Area -->
    <div class="min-h-screen transition-all duration-300 lg:ml-[260px]">
      <!-- Header -->
      <app-header (sidebarToggle)="onSidebarToggle()" />

      <!-- Page Content -->
      <main class="p-6 bg-slate-50 min-h-[calc(100vh-4rem)]">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class DashboardLayoutComponent {
  @ViewChild('sidebar') sidebar!: SidebarComponent;

  onSidebarToggle(): void {
    this.sidebar.toggle();
  }
}
