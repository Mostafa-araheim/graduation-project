import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { InventoryService } from '../../../core/services/inventory.service';
import {
  PharmacyProductDto,
  InventoryFilterParams,
  AddPharmacyProductRequest,
  UpdatePharmacyProductRequest,
} from '../../../core/models/inventory.model';
import {
  AvailabilityStatus,
  AVAILABILITY_STATUSES,
} from '../../../core/models/enums.model';
import { PageResponse } from '../../../core/models/api-response.model';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EgpCurrencyPipe } from '../../../shared/pipes/currency-egp.pipe';
export interface Product {
  id: number;
  name: string;
  description: string;
  requiresPrescription: boolean;
  dosageForm: string;
  strength: string;
  manufacturer: string;
  category: string;
}
@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    StatusBadgeComponent,
    LoadingSpinnerComponent,
    EgpCurrencyPipe,
  ],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div
        class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
      >
        <div>
          <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">
            Inventory
          </h1>
          <p class="text-sm text-gray-500 mt-1">
            Manage your pharmacy products
          </p>
        </div>
        <button
          (click)="showAddProduct = !showAddProduct"
          class="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-emerald-500
                       text-white font-medium text-sm shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40
                       transition-all duration-300 hover:-translate-y-0.5"
        >
          <svg
            class="w-5 h-5"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          Add Product
        </button>
      </div>

      <!-- Add Product Section -->
      @if (showAddProduct) {
        <div
          class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm space-y-4"
        >
          <h3 class="text-lg font-semibold text-gray-900 font-['Outfit']">
            Add Product to Inventory
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              
              <div class="relative">
                <label class="block text-sm font-medium text-gray-700 mb-1.5">
                  Product
                </label>

                <input
                  type="text"
                  [(ngModel)]="productSearch"
                  (input)="filterProducts()"
                  (focus)="showProductDropdown = true"
                  placeholder="Search product by name, category..."
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
           focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 outline-none transition-all text-sm"
                />

                @if (showProductDropdown) {
                  <div
                    class="absolute z-50 w-full bg-white border border-gray-200 rounded-xl mt-1 max-h-60 overflow-auto shadow-lg"
                  >
                    @if (filteredProducts.length === 0) {
                      <div class="px-4 py-2 text-sm text-gray-500">
                        No products found
                      </div>
                    }

                    @for (p of filteredProducts; track p.id) {
                      <div
                        (click)="selectProduct(p)"
                        class="px-4 py-2 hover:bg-emerald-50 cursor-pointer text-sm"
                      >
                        <div class="font-medium">{{ p.name }}</div>
                        <div class="text-xs text-gray-500">
                          {{ p.category }} • {{ p.manufacturer }}
                        </div>
                      </div>
                    }
                  </div>
                }
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5"
                >Quantity</label
              >
              <input
                [(ngModel)]="newProduct.quantity"
                type="number"
                placeholder="0"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                            focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 outline-none transition-all text-sm"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5"
                >Price (EGP)</label
              >
              <input
                [(ngModel)]="newProduct.price"
                type="number"
                step="0.01"
                placeholder="0.00"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                            focus:ring-2 focus:ring-emerald-500/20 focus:border-emerald-500 outline-none transition-all text-sm"
              />
            </div>
          </div>
          <div class="flex gap-3 justify-end">
            <button
              (click)="showAddProduct = false"
              class="px-4 py-2 rounded-xl border border-gray-200 text-gray-700 text-sm font-medium
                           hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              (click)="addProduct()"
              class="px-4 py-2 rounded-xl bg-emerald-600 text-white text-sm font-medium
                           hover:bg-emerald-700 transition-colors"
            >
              Add Product
            </button>
          </div>
        </div>
      }

      <!-- Filters -->
      <div class="bg-white rounded-2xl border border-gray-100 p-4 shadow-sm">
        <div class="flex flex-col md:flex-row gap-3">
          <div class="flex-1">
            <input
              [(ngModel)]="searchTerm"
              (ngModelChange)="onFilterChange()"
              type="text"
              placeholder="Search products..."
              class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                          focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
            />
          </div>
          <select
            [(ngModel)]="statusFilter"
            (ngModelChange)="onFilterChange()"
            class="px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50 text-sm
                         focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all"
          >
            <option value="">All Statuses</option>
            @for (status of availabilityStatuses; track status) {
              <option [value]="status">{{ status }}</option>
            }
          </select>
          <input
            [(ngModel)]="categoryFilter"
            (ngModelChange)="onFilterChange()"
            type="text"
            placeholder="Filter by category..."
            class="px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50 text-sm
                        focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all md:w-48"
          />
        </div>
      </div>

      <!-- Loading -->
      @if (loading()) {
        <app-loading-spinner message="Loading inventory..." />
      }

      <!-- Table -->
      @if (!loading() && products().length > 0) {
        <div
          class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
        >
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-gray-100">
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Product
                  </th>
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Category
                  </th>
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Price
                  </th>
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Quantity
                  </th>
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Status
                  </th>
                  <th
                    class="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Dosage
                  </th>
                  <th
                    class="text-right text-xs font-semibold text-gray-500 uppercase tracking-wider px-6 py-4"
                  >
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-50">
                @for (
                  product of products();
                  track product.pharmacy_product_id
                ) {
                  <tr class="hover:bg-gray-50/50 transition-colors">
                    <!-- Product -->
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-3">
                        @if (product.product_image) {
                          <img
                            [src]="product.product_image"
                            [alt]="product.product_name"
                            class="w-10 h-10 rounded-lg object-cover"
                          />
                        } @else {
                          <div
                            class="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center"
                          >
                            <svg
                              class="w-5 h-5 text-gray-400"
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
                          </div>
                        }
                        <div>
                          <p class="text-sm font-medium text-gray-900">
                            {{ product.product_name }}
                          </p>
                          <p class="text-xs text-gray-400">
                            {{ product.manufacturer }}
                          </p>
                        </div>
                      </div>
                    </td>
                    <!-- Category -->
                    <td class="px-6 py-4">
                      <span class="text-sm text-gray-600">{{
                        product.category_name
                      }}</span>
                    </td>
                    <!-- Price -->
                    <td class="px-6 py-4">
                      @if (editingId() === product.pharmacy_product_id) {
                        <input
                          [(ngModel)]="editPrice"
                          type="number"
                          step="0.01"
                          class="w-24 px-2 py-1 rounded-lg border border-blue-300 text-sm focus:ring-2
                                      focus:ring-blue-500/20 outline-none"
                        />
                      } @else {
                        <span class="text-sm font-medium text-gray-900">{{
                          product.price | egp
                        }}</span>
                      }
                    </td>
                    <!-- Quantity -->
                    <td class="px-6 py-4">
                      @if (editingId() === product.pharmacy_product_id) {
                        <input
                          [(ngModel)]="editQuantity"
                          type="number"
                          class="w-20 px-2 py-1 rounded-lg border border-blue-300 text-sm focus:ring-2
                                      focus:ring-blue-500/20 outline-none"
                        />
                      } @else {
                        <span class="text-sm text-gray-600">{{
                          product.quantity
                        }}</span>
                      }
                    </td>
                    <!-- Status -->
                    <td class="px-6 py-4">
                      <app-status-badge [status]="getStockStatus(product)" />
                    </td>
                    <!-- Dosage -->
                    <td class="px-6 py-4">
                      <span class="text-sm text-gray-600">{{
                        product.dosage_form
                      }}</span>
                    </td>
                    <!-- Actions -->
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        @if (editingId() === product.pharmacy_product_id) {
                          <button
                            (click)="saveEdit(product)"
                            class="p-1.5 rounded-lg bg-emerald-50 text-emerald-600 hover:bg-emerald-100 transition-colors"
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
                                d="M5 13l4 4L19 7"
                              />
                            </svg>
                          </button>
                          <button
                            (click)="cancelEdit()"
                            class="p-1.5 rounded-lg bg-gray-50 text-gray-600 hover:bg-gray-100 transition-colors"
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
                                d="M6 18L18 6M6 6l12 12"
                              />
                            </svg>
                          </button>
                        } @else {
                          <button
                            (click)="startEdit(product)"
                            class="p-1.5 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 transition-colors"
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
                          </button>
                          <button
                            (click)="deleteProduct(product)"
                            class="p-1.5 rounded-lg bg-rose-50 text-rose-600 hover:bg-rose-100 transition-colors"
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
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          @if (pageData()) {
            <div
              class="flex items-center justify-between px-6 py-4 border-t border-gray-100"
            >
              <p class="text-sm text-gray-500">
                Showing {{ products().length }} of
                {{ pageData()!.totalElements }} products
              </p>
              <div class="flex items-center gap-2">
                <button
                  (click)="goToPage(currentPage() - 1)"
                  [disabled]="currentPage() === 0"
                  class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600
                               hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                <span class="px-3 py-1.5 text-sm font-medium text-gray-700">
                  Page {{ currentPage() + 1 }} of {{ pageData()!.totalPages }}
                </span>
                <button
                  (click)="goToPage(currentPage() + 1)"
                  [disabled]="pageData()!.last"
                  class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm text-gray-600
                               hover:bg-gray-50 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </div>
            </div>
          }
        </div>
      }

      <!-- Empty -->
      @if (!loading() && products().length === 0) {
        <div
          class="flex flex-col items-center justify-center py-20 text-center"
        >
          <div
            class="w-20 h-20 rounded-2xl bg-emerald-50 flex items-center justify-center mb-4"
          >
            <svg
              class="w-10 h-10 text-emerald-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="1.5"
                d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
              />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">No products found</h3>
          <p class="text-sm text-gray-500 mt-1">
            Add products to your pharmacy inventory to get started.
          </p>
        </div>
      }
    </div>
  `,
})
export class InventoryListComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  private route = inject(ActivatedRoute);
  // private productService  = inject(this.inventoryService)
  pharmacyId = 0;
  products = signal<PharmacyProductDto[]>([]);
  pageData = signal<PageResponse<PharmacyProductDto> | null>(null);
  loading = signal(true);
  currentPage = signal(0);
  editingId = signal<number | null>(null);

  searchTerm = '';
  statusFilter = '';
  categoryFilter = '';
  availabilityStatuses = AVAILABILITY_STATUSES;

  showAddProduct = false;
  newProduct: AddPharmacyProductRequest = {
    product_id: 0,
    quantity: 0,
    price: 0,
  };
  editPrice = 0;
  editQuantity = 0;
  allProducts: Product[] = [];
  filteredProducts: Product[] = [];

  productSearch = '';
  showProductDropdown = false;
  ngOnInit(): void {
    this.pharmacyId = +this.route.snapshot.paramMap.get('pharmacyId')!;
    this.loadInventory();
    this.loadProducts();
  }

  filterProducts(): void {
    const value = this.productSearch.toLowerCase();

    this.filteredProducts = this.allProducts.filter(
      (p) =>
        p.name.toLowerCase().includes(value) ||
        p.category.toLowerCase().includes(value) ||
        p.manufacturer.toLowerCase().includes(value) ||
        p.id.toString().includes(value),
    );
  }
  selectProduct(product: Product): void {
    this.newProduct.product_id = product.id;
    this.productSearch = product.name;
    this.showProductDropdown = false;
  }
  closeDropdown(): void {
    this.showProductDropdown = false;
  }
  loadProducts(): void {
    this.inventoryService.getUniqueProducts().subscribe({
      next: (res) => {
        this.allProducts = res.data;
        this.filteredProducts = res.data;
      },
    });
  }
  loadInventory(): void {
    this.loading.set(true);
    const filters: InventoryFilterParams = {};
    if (this.searchTerm) filters.productName = this.searchTerm;
    if (this.statusFilter)
      filters.availabilityStatus = this.statusFilter as AvailabilityStatus;
    if (this.categoryFilter) filters.categoryName = this.categoryFilter;

    this.inventoryService
      .getInventory(this.pharmacyId, filters, {
        page: this.currentPage(),
        size: 10,
      })
      .subscribe({
        next: (page) => {
          this.products.set(page.content);
          this.pageData.set(page);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  onFilterChange(): void {
    this.currentPage.set(0);
    this.loadInventory();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadInventory();
  }

  getStockStatus(product: PharmacyProductDto): string {
    if (product.quantity === 0) return 'OutOfStock';
    if (product.quantity <= 10) return 'LimitedSupply';
    return 'Available';
  }

  startEdit(product: PharmacyProductDto): void {
    this.editingId.set(product.pharmacy_product_id);
    this.editPrice = product.price;
    this.editQuantity = product.quantity;
  }

  cancelEdit(): void {
    this.editingId.set(null);
  }

  saveEdit(product: PharmacyProductDto): void {
    const request: UpdatePharmacyProductRequest = {
      price: this.editPrice,
      quantity: this.editQuantity,
    };
    this.inventoryService
      .updateProduct(this.pharmacyId, product.product_id, request)
      .subscribe({
        next: () => {
          this.editingId.set(null);
          this.loadInventory();
        },
      });
  }

  addProduct(): void {
    if (!this.newProduct.product_id) return;
    if (this.newProduct.quantity <= 0) return;
    if (this.newProduct.price <= 0) return;
    this.inventoryService
      .addProduct(this.pharmacyId, this.newProduct)
      .subscribe({
        next: () => {
          this.showAddProduct = false;
          this.newProduct = { product_id: 0, quantity: 0, price: 0 };
          this.loadInventory();
        },
      });
  }

  deleteProduct(product: PharmacyProductDto): void {
    if (window.confirm(`Delete "${product.product_name}" from inventory?`)) {
      this.inventoryService
        .deleteProduct(this.pharmacyId, product.product_id)
        .subscribe({
          next: () => this.loadInventory(),
        });
    }
  }
}
