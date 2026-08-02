import { LoaderComponent } from './../../../Shared/loader/loader.component';
import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SearchFilterComponent } from '../../../Shared/search-filter/search-filter.component';
import { ProductCardComponent } from '../../../Shared/product-card/product-card.component';
import {
  LucideAngularModule,
  MapPin,
  Check,
  ChevronDown,
  Grid3x3,
  List,
} from 'lucide-angular';
import { PaginationComponent } from '../../../Shared/pagination/pagination.component';
import { ProductService } from '../../services/Product/product.service';
import { CartService } from '../../services/cart/cart.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../services/auth/auth.service';
import { switchMap } from 'rxjs';
import { Environment } from '../../../Environment/environment';

export interface Product {
  brand_id: number;
  brand_name: string;
  category_id: number;
  category_name: string;
  description: string;
  dosage_form: string;
  in_stock: boolean;
  manufacturer: string;
  pharmacy_name: string;
  pharmacy_product_id: number;
  price: number;
  product_id: number;
  product_image: string;
  product_name: string;
  quantity: number;
  requires_prescription: boolean;
  strength: string;
  pharmacy_id: number;
}

@Component({
  selector: 'app-medicines',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    SearchFilterComponent,
    ProductCardComponent,
    LucideAngularModule,
    PaginationComponent,
    ToastModule,
    LoaderComponent,
  ],
  providers: [MessageService],
  templateUrl: './medicines.component.html',
  styleUrl: './medicines.component.css',
})
export class MedicinesComponent {
  constructor(
    private _productService: ProductService,
    private _cartService: CartService,
    private _messageService: MessageService,
    private _authService: AuthService,
  ) {}

  // ================= ICONS =================
  mapPin = MapPin;
  check = Check;
  chevronDown = ChevronDown;
  grid = Grid3x3;
  list = List;

  // ================= STATE =================
  currentPage = 0;
  totalPages = 0;
  searchQuery = '';

  base = Environment.base;
  // dropdown states
  isFilterationOpen = signal(false);
  mobileViewForFilter = signal(false);

  // view mode
  viewMode = signal<'grid' | 'list'>('grid');

  // categories from filter
  selectedCategory = signal<string | null>(null);

  // stock filter from filter component
  inStockOnly = signal<boolean>(false);

  // Add loading state
  isLoading = signal<boolean>(false);

  // sorting (FIXED)
  selectedFilter = signal<
    'Nearest First' | 'Price: High to Low' | 'Price: Low to High' | 'Name A-Z'
  >('Nearest First');

  filterationOptions = [
    'Nearest First',
    'Price: Low to High',
    'Price: High to Low',
    'Name A-Z',
  ] as const;

  // data
  products = signal<Product[]>([]);

  // ================= INIT =================
  ngOnInit() {
    this.fetchProducts();
  }

  // ================= API =================
  fetchProducts() {
    this.isLoading.set(true);

    const sortMap: any = {
      'Price: Low to High': 'price,asc',
      'Price: High to Low': 'price,desc',
      'Name A-Z': 'name',
      'Nearest First': null,
    };

    let sortValue = sortMap[this.selectedFilter()];

    // 🔥 inStock should sort using availabilityStatus
    if (this.inStockOnly()) {
      sortValue = 'availabilityStatus';
    }

    // Log the parameters being sent to API
    console.log('=== FETCHING PRODUCTS ===');
    console.log('Search Query:', this.searchQuery);
    console.log('Selected Category:', this.selectedCategory());
    console.log(
      'Sort Filter:',
      this.selectedFilter(),
      '-> API sort:',
      sortValue,
    );
    console.log('Stock Filter:', this.inStockOnly());

    this._productService
      .getProducts(
        this.searchQuery || null,
        this.selectedCategory(),
        sortValue,
        this.currentPage,
      )
      .subscribe({
        next: (res: any) => {
          this.totalPages = res.data.totalPages;
          console.log('API Response:', res);

          let products = [...res.data.content];

          this.products.set(products);

          console.log('Final products count:', products.length);
          console.log('First product sample:', products[0]);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('API Error:', err);
          this.isLoading.set(false);
        },
      });
  }

  // ================= FILTER HANDLERS =================

  // Handle category change from SearchFilterComponent
  onCategoryChange(category: string | null) {
    // console.log('🔵 Category changed in parent:', category);
    this.selectedCategory.set(category);
    console.log(this.selectedCategory());
    this.currentPage = 0;
    this.fetchProducts(); // This should trigger API call
  }

  // Handle stock filter change from SearchFilterComponent
  onStockFilterChange(inStockOnly: boolean) {
    console.log('🟢 Stock filter changed in parent:', inStockOnly);
    this.inStockOnly.set(inStockOnly);
    this.currentPage = 0;
    this.fetchProducts(); // This should trigger API call
  }

  // ================= SEARCH =================
  onSearch(value: string) {
    console.log('🔍 Search changed:', value);
    this.searchQuery = value;
    this.currentPage = 0;
    this.fetchProducts();
  }

  // ================= FILTER (Sorting) =================
  selectFilter(filter: any) {
    console.log('📊 Sort filter changed:', filter);
    this.selectedFilter.set(filter);
    this.isFilterationOpen.set(false);
    this.currentPage = 0;
    this.fetchProducts();
  }

  // ================= TOGGLES =================
  toggleFilterationDropdown() {
    this.isFilterationOpen.update((v) => !v);
  }

  toggleMobileFilteration() {
    this.mobileViewForFilter.update((v) => !v);
  }

  toggleView(mode: 'grid' | 'list') {
    this.viewMode.set(mode);
  }

  // ================= PAGINATION =================
  onPageChange(page: number) {
    this.currentPage = page;
    this.fetchProducts();
  }

  // ================= CLEAR =================
  clearFilters() {
    console.log('🧹 Clearing all filters');
    this.searchQuery = '';
    this.selectedCategory.set(null);
    this.inStockOnly.set(false);
    this.selectedFilter.set('Nearest First');
    this.currentPage = 0;
    this.fetchProducts();
  }

  handleAddToCart(product: Product) {
    console.log(product);

    this._cartService.addToCart(product).subscribe({
      next: () => {
        this._messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Added to cart successfully',
          life: 3000,
        });
      },
      error: () => {
        this._messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Something went wrong!',
          life: 3000,
        });
      },
    });
  }
}
