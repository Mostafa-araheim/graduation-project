import { Routes } from '@angular/router';

export const ORDER_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./orders-list/orders-list.component').then(m => m.OrdersListComponent) }
];
