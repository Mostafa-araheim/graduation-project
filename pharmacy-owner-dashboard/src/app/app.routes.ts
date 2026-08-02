import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '',
    loadComponent: () => import('./features/dashboard/dashboard-layout/dashboard-layout.component')
      .then(m => m.DashboardLayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard-overview/dashboard-overview.component')
          .then(m => m.DashboardOverviewComponent)
      },
      {
        path: 'pharmacies',
        loadChildren: () => import('./features/pharmacies/pharmacies.routes').then(m => m.PHARMACY_ROUTES)
      },
      {
        path: 'inventory/:pharmacyId',
        loadChildren: () => import('./features/inventory/inventory.routes').then(m => m.INVENTORY_ROUTES)
      },
      {
        path: 'orders/:pharmacyId',
        loadChildren: () => import('./features/orders/orders.routes').then(m => m.ORDER_ROUTES)
      },
      {
        path: 'analytics',
        loadComponent: () => import('./features/analytics/analytics-overview/analytics-overview.component')
          .then(m => m.AnalyticsOverviewComponent)
      },
      {
        path: 'reviews/:pharmacyId',
        loadChildren: () => import('./features/reviews/reviews.routes').then(m => m.REVIEW_ROUTES)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile-view/profile-view.component')
          .then(m => m.ProfileViewComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
