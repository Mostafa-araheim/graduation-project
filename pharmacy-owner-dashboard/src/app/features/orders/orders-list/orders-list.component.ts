import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { OwnerOrderResponse } from '../../../core/models/order.model';
import { PageResponse } from '../../../core/models/api-response.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EgpCurrencyPipe } from '../../../shared/pipes/currency-egp.pipe';

@Component({
  selector: 'app-orders-list',
  standalone: true,
  imports: [CommonModule, StatusBadgeComponent, LoadingSpinnerComponent, EgpCurrencyPipe],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div>
        <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">Orders</h1>
        <p class="text-sm text-gray-500 mt-1">View and manage pharmacy orders</p>
      </div>

      @if (loading()) {
        <app-loading-spinner message="Loading orders..." />
      }

      @if (!loading() && orders().length > 0) {
        <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-gray-100">
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4 w-8"></th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Order ID</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Customer</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Total</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Delivery</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Payment</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Status</th>
                  <th class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4">Items</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (order of orders(); track order.orderId) {
                  <!-- Main Row -->
                  <tr (click)="toggleExpand(order.orderId)"
                      class="hover:bg-gray-50/50 transition-colors cursor-pointer">
                    <td class="px-6 py-4">
                      <svg class="w-4 h-4 text-gray-400 transition-transform duration-200"
                           [class.rotate-90]="expandedOrderId() === order.orderId"
                           fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                      </svg>
                    </td>
                    <td class="px-6 py-4">
                      <span class="text-sm font-mono font-medium text-gray-900">#{{ order.orderId }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <span class="text-sm text-gray-700">{{ order.customerName }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <span class="text-sm font-semibold text-gray-900">{{ order.totalPrice | egp }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <span class="inline-flex items-center gap-1.5 text-xs font-medium px-2.5 py-1 rounded-full"
                            [class]="order.deliveryType === 'DELIVERY'
                              ? 'bg-blue-50 text-blue-700'
                              : 'bg-gray-50 text-gray-700'">
                        {{ order.deliveryType }}
                      </span>
                    </td>
                    <td class="px-6 py-4">
                      <span class="text-sm text-gray-600">{{ order.paymentMethod }}</span>
                    </td>
                    <td class="px-6 py-4">
                      <app-status-badge [status]="order.status" />
                    </td>
                    <td class="px-6 py-4">
                      <span class="inline-flex items-center justify-center w-7 h-7 rounded-full bg-gray-100 text-xs font-semibold text-gray-700">
                        {{ order.items.length }}
                      </span>
                    </td>
                  </tr>

                  <!-- Expanded Row -->
                  @if (expandedOrderId() === order.orderId) {
                    <tr>
                      <td colspan="8" class="px-6 py-0">
                        <div class="py-4 pl-12 pr-4 bg-gray-50/50 rounded-xl my-2 animate-in">
                          <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">Order Items</h4>
                          <table class="w-full">
                            <thead>
                              <tr class="text-xs text-gray-400">
                                <th class="text-left pb-2 font-medium">Product</th>
                                <th class="text-left pb-2 font-medium">Qty</th>
                                <th class="text-left pb-2 font-medium">Price</th>
                                <th class="text-right pb-2 font-medium">Subtotal</th>
                              </tr>
                            </thead>
                            <tbody class="divide-y divide-gray-100">
                              @for (item of order.items; track item.orderItemId) {
                                <tr>
                                  <td class="py-2 text-sm text-gray-700">{{ item.productName }}</td>
                                  <td class="py-2 text-sm text-gray-600">{{ item.quantity }}</td>
                                  <td class="py-2 text-sm text-gray-600">{{ item.priceAtPurchase | egp }}</td>
                                  <td class="py-2 text-sm font-medium text-gray-900 text-right">{{ item.subtotal | egp }}</td>
                                </tr>
                              }
                            </tbody>
                          </table>
                        </div>
                      </td>
                    </tr>
                  }
                }
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          @if (pageData()) {
            <div class="flex items-center justify-between px-6 py-4 border-t border-gray-100">
              <p class="text-sm text-gray-500">
                Showing {{ orders().length }} of {{ pageData()!.totalElements }} orders
              </p>
              <div class="flex items-center gap-2">
                <button (click)="goToPage(currentPage() - 1)" [disabled]="currentPage() === 0"
                        class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600
                               hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                  Previous
                </button>
                <span class="px-3 py-1.5 text-sm font-medium text-gray-700">
                  Page {{ currentPage() + 1 }} of {{ pageData()!.totalPages }}
                </span>
                <button (click)="goToPage(currentPage() + 1)" [disabled]="pageData()!.last"
                        class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600
                               hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                  Next
                </button>
              </div>
            </div>
          }
        </div>
      }

      <!-- Empty -->
      @if (!loading() && orders().length === 0) {
        <div class="flex flex-col items-center justify-center py-20 text-center">
          <div class="w-20 h-20 rounded-2xl bg-purple-50 flex items-center justify-center mb-4">
            <svg class="w-10 h-10 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">No orders yet</h3>
          <p class="text-sm text-gray-500 mt-1">Orders will appear here once customers start placing them.</p>
        </div>
      }
    </div>
  `,
  styles: [`
    .animate-in {
      animation: slideDown 0.2s ease-out;
    }
    @keyframes slideDown {
      from { opacity: 0; transform: translateY(-8px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class OrdersListComponent implements OnInit {
  private orderService = inject(OrderService);
  private route = inject(ActivatedRoute);

  pharmacyId = 0;
  orders = signal<OwnerOrderResponse[]>([]);
  pageData = signal<PageResponse<OwnerOrderResponse> | null>(null);
  loading = signal(true);
  currentPage = signal(0);
  expandedOrderId = signal<number | null>(null);

  ngOnInit(): void {
    this.pharmacyId = +this.route.snapshot.paramMap.get('pharmacyId')!;
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    this.orderService.getPharmacyOrders(this.pharmacyId, { page: this.currentPage(), size: 10 }).subscribe({
      next: (page) => {
        this.orders.set(page.content);
        this.pageData.set(page);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  toggleExpand(orderId: number): void {
    this.expandedOrderId.set(this.expandedOrderId() === orderId ? null : orderId);
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadOrders();
  }
}
