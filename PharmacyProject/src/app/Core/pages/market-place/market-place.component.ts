import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { computed, signal } from '@angular/core';
import { SearchFilterComponent } from '../../../Shared/search-filter/search-filter.component';
import { ProductService } from '../../services/Product/product.service';
import { PaginationComponent } from '../../../Shared/pagination/pagination.component';
import { Environment } from '../../../Environment/environment';
import { LoaderComponent } from '../../../Shared/loader/loader.component';
import { CategoryService } from '../../services/Category/category.service';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';

export interface Data {
  content: Listing[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface Listing {
  listingId: number;
  productId: number;
  productName: string;
  city: string;
  sellerId: number;
  sellerName: string;
  sellerPhoneNumber: string;
  categoryName: string;
  condition: string;
  quantity: number;
  price: number;
  expiryDate: string;
  description: string;
  imageUrl: string;
  status: string;
  createdAt: string;
}

export interface Category {
  category_name: string;
  image_url: string;
  item_count: number;
}

@Component({
  selector: 'app-market-place',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PaginationComponent,
    LoaderComponent,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
  ],
  templateUrl: './market-place.component.html',
  styleUrl: './market-place.component.css',
})
export class MarketPlaceComponent {
  /**
   *
   */
  constructor(
    private _productService: ProductService,
    private _categories: CategoryService,
  ) {}
  ngOnInit() {
    this.isLoading = true;
    this.getListedProducts();
    this.getCategories();
    this.displayedLocations = this.locations.slice(0, 5);
  }
  categories: Category[] = [];

  locations = [
    'All Locations',
    'Cairo',
    'Giza',
    'Alexandria',
    'Dakahlia',
    'Red Sea',
    'Beheira',
    'Fayoum',
    'Gharbia',
    'Ismailia',
    'Menofia',
    'Minya',
    'Qalyubia',
    'New Valley',
    'Suez',
    'Aswan',
    'Assiut',
    'Beni Suef',
    'Port Said',
    'Damietta',
    'Sharkia',
    'South Sinai',
    'Kafr El Sheikh',
    'Matrouh',
    'Luxor',
    'Qena',
    'North Sinai',
    'Sohag',
  ];
  displayedLocations: string[] = [];
  sortOptions = [
    { value: 'newest', label: 'Newest First' },
    { value: 'oldest', label: 'Oldest First' },
    { value: 'price_asc', label: 'Price: Low to High' },
    { value: 'price_desc', label: 'Price: High to Low' },
  ];

  base = Environment.base;
  listings: Listing[] = [];
  currentPage: number = 0;
  totalPages = 0;
  searchTerm = signal('');
  selectedCategory = signal('');
  selectedLocation = signal('');
  isLoading: boolean = false;
  minPrice = signal(0);
  maxPrice = signal(1000);

  sortBy = signal('Newest First');

  // =========================
  // METHODS
  // =========================

  getListedProducts() {
    this.isLoading = true;
    this._productService
      .getAllUserListedProduct(
        this.selectedCategory(),
        this.selectedLocation(),
        this.searchTerm(),
        this.currentPage,
        4,
      )
      .subscribe({
        next: (res) => {
          console.log(res);
          this.totalPages = res.totalPages;
          this.listings = res.content;
          this.isLoading = false;
        },
        error: (err) => {
          this.isLoading = false;
        },
      });
  }

  getCategories() {
    this._categories.GetCategories().subscribe({
      next: (res) => {
        console.log(res);
        this.categories = res.data;
      },
    });
  }

  onFilter(event: any) {
    const query = event.filter?.toLowerCase() || '';

    if (!query) {
      // show only first 5 when search is empty
      this.displayedLocations = this.locations.slice(0, 5);
      return;
    }

    // search in all products
    this.displayedLocations = this.locations.filter((p) =>
      p.toLowerCase().includes(query),
    );
  }

  selectCategory(category: string): void {
    this.selectedCategory.set(category);
    this.getListedProducts();
  }

  updateSearch(value: string): void {
    this.searchTerm.set(value);
    this.getListedProducts();
  }

  updateLocation(value: string): void {
    this.selectedLocation.set(value);
    if (this.selectedLocation() === 'All Locations')
      this.selectedLocation.set('');
    this.getListedProducts();
  }

  updateSort(value: string): void {
    this.sortBy.set(value);
  }

  updateMinPrice(value: number): void {
    this.minPrice.set(value);
  }

  updateMaxPrice(value: number): void {
    this.maxPrice.set(value);
  }

  resetFilters(): void {
    this.searchTerm.set('');
    this.selectedCategory.set('');
    this.selectedLocation.set('');

    this.minPrice.set(0);
    this.maxPrice.set(1000);

    this.sortBy.set('Newest First');
    this.getListedProducts();
  }

  contactSeller(listing: Listing): void {
    console.log('Contact seller:', listing);
  }

  viewDetails(listing: Listing): void {
    console.log('View details:', listing);
  }

  createListing(): void {
    console.log('Create new listing');
  }

  selectedListing = signal<Listing | null>(null);
  contactListing = signal<Listing | null>(null);
  // ===================== // MODAL METHODS

  openDetails(listing: Listing): void {
    this.selectedListing.set(listing);
  }
  closeDetails(): void {
    this.selectedListing.set(null);
  }
  openContact(listing: Listing): void {
    this.contactListing.set(listing);
  }
  closeContact(): void {
    this.contactListing.set(null);
  }
  onPageChange(page: number) {
    this.currentPage = page;
    this.getListedProducts();
  }

  timeAgo(dateString: string): string {
    const now = new Date();
    const created = new Date(dateString);

    const seconds = Math.floor((now.getTime() - created.getTime()) / 1000);

    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30);
    const years = Math.floor(days / 365);

    if (seconds < 60) {
      return 'Just now';
    }

    if (minutes < 60) {
      return `${minutes}m ago`;
    }

    if (hours < 24) {
      return `${hours}h ago`;
    }

    if (days < 30) {
      return `${days}d ago`;
    }

    if (months < 12) {
      return `${months}mo ago`;
    }

    return `${years}y ago`;
  }

  contactFromDetails(listing: Listing): void {
    this.selectedListing.set(null);
    setTimeout(() => {
      this.contactListing.set(listing);
    }, 150);
  }
}
