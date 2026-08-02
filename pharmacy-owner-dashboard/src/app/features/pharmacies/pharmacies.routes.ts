import { Routes } from '@angular/router';

export const PHARMACY_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./pharmacy-list/pharmacy-list.component').then(m => m.PharmacyListComponent) },
  { path: 'new', loadComponent: () => import('./pharmacy-form/pharmacy-form.component').then(m => m.PharmacyFormComponent) },
  { path: ':id', loadComponent: () => import('./pharmacy-detail/pharmacy-detail.component').then(m => m.PharmacyDetailComponent) },
  { path: ':id/edit', loadComponent: () => import('./pharmacy-form/pharmacy-form.component').then(m => m.PharmacyFormComponent) },
];
