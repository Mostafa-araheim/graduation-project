import { Component, computed, OnInit, signal, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  ChevronDown,
  X,
  Filter,
  Check,
} from 'lucide-angular';
import { CategoryService } from '../../Core/services/Category/category.service';

interface Category {
  category_name: string;
  image_url: string;
  item_count: number;
}

export interface FilterOptions {
  category: string | null;
  inStockOnly: boolean;
}

@Component({
  selector: 'app-search-filter',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './search-filter.component.html',
  styleUrl: './search-filter.component.css',
})
export class SearchFilterComponent implements OnInit {
  chevronDown = ChevronDown;
  x = X;
  filter = Filter;
  check = Check;

  // ================= OUTPUTS =================
  filterChange = output<FilterOptions>(); // Emit when filters change
  categoryChange = output<string | null>(); // Emit when only category changes
  stockFilterChange = output<boolean>(); // Emit when only stock filter changes

  constructor(private _categoryService: CategoryService) {}

  ngOnInit(): void {
    this._categoryService.GetCategories().subscribe({
      next: (res) => {
        this.categories = res.data;
      },
    });
  }

  categories!: Category[];

  // CHANGE: Single category instead of array
  selectedCategory = signal<string | null>(null);
  distance = signal(10);
  priceMin = signal(0);
  priceMax = signal(100);
  inStockOnly = signal(false);
  isMobileOpen = signal(false);

  // collapsible state
  openSections = signal<Record<string, boolean>>({
    categories: true,
    distance: true,
    price: true,
  });

  // ================= COMPUTED =================
  activeFiltersCount = computed(() => {
    let count = this.selectedCategory() ? 1 : 0;

    if (this.distance() !== 10) count++;
    if (this.priceMin() !== 0 || this.priceMax() !== 100) count++;
    if (this.inStockOnly()) count++;

    return count;
  });

  // ================= METHODS =================
  toggleSection(section: string) {
    this.openSections.update((s) => ({
      ...s,
      [section]: !s[section],
    }));
  }

  toggleMobile() {
    this.isMobileOpen.update((v) => !v);
  }

  // CHANGE: Single category selection (radio behavior)
  selectCategory(category: string | null) {
    // If same category is clicked, deselect it
    const newCategory = this.selectedCategory() === category ? null : category;
    this.selectedCategory.set(newCategory);
    this.categoryChange.emit(newCategory);
    // Emit the change
    this.emitFilters();
  }

  // CHANGE: Emit only category change
  emitCategoryChange() {
    this.categoryChange.emit(this.selectedCategory());
    this.filterChange.emit({
      category: this.selectedCategory(),
      inStockOnly: this.inStockOnly(),
    });
  }

  // CHANGE: Emit only stock filter change
  onStockFilterChange(value: boolean) {
    this.inStockOnly.set(value);
    this.stockFilterChange.emit(value);
    this.emitFilters();
  }

  // Emit all filters
  emitFilters() {
    this.filterChange.emit({
      category: this.selectedCategory(),
      inStockOnly: this.inStockOnly(),
    });
  }

  clearAll() {
    this.selectedCategory.set(null);
    this.categoryChange.emit(null);

    this.distance.set(10);
    this.priceMin.set(0);
    this.priceMax.set(100);
    this.inStockOnly.set(false);

    this.stockFilterChange.emit(false);

    this.emitFilters();
  }

  applyFilters() {
    // Just emit the current filters
    this.emitFilters();

    // Close mobile view if open
    if (this.isMobileOpen()) {
      this.isMobileOpen.set(false);
    }

    console.log('Filters applied:', {
      category: this.selectedCategory(),
      inStockOnly: this.inStockOnly(),
    });
  }
}
