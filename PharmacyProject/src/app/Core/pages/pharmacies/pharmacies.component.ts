import { PharmaciesService } from './../../services/Pharmacies/pharmacies.service';
import { CommonModule, DatePipe, isPlatformBrowser } from '@angular/common';
import {
  Component,
  computed,
  ElementRef,
  HostListener,
  Inject,
  OnInit,
  PLATFORM_ID,
  signal,
  ViewChild,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  LucideAngularModule,
  Search,
  MapPin,
  Shield,
  Truck,
  Clock,
  Upload,
  Pill,
  Store,
  Star,
  ArrowRight,
  ChevronRight,
  Users,
  Navigation,
  ChevronDown,
  Check,
} from 'lucide-angular';
import { PharmacyMapComponent } from '../../../Shared/pharmacies-map/pharmacies-map.component';
import { log } from 'console';
import { PaginationComponent } from '../../../Shared/pagination/pagination.component';
import { LoaderComponent } from '../../../Shared/loader/loader.component';
import { Environment } from '../../../Environment/environment';
// import { PharmaciesService } from '../../services/Pharmacies/pharmacies.service';

interface Content {
  pharmacy_id: number;
  name: string;
  image_url: string;
  rating?: number;
  distance_in_kilometers: any;
  opening_time: string;
  closing_time: string;
  latitude: number;
  longitude: number;
  review_count?: number;
  is_closed: boolean;
  address: string;
}

interface Pharmacy {
  id: number;
  name: string;
  image: string;
  address: string;
  distance: number;
  rating: number;
  reviews: number;
  deliveryTime: string;
  categories: number;
  closingText: string;
  isFeatured?: boolean;
  isClosed?: boolean;
  lat: number;
  lng: number;
}

interface SortOptions {
  label: string;
  value: 'nearest' | 'rating';
}

interface StatusOptions {
  label: string;
  value: 'all' | 'open';
}

@Component({
  selector: 'app-pharmacies',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    LucideAngularModule,
    PharmacyMapComponent,
    DatePipe,
    RouterLink,
    PaginationComponent,
    LoaderComponent,
  ],
  templateUrl: './pharmacies.component.html',
  styleUrl: './pharmacies.component.css',
})
export class PharmaciesComponent implements OnInit {
  constructor(
    private _pharmaciesService: PharmaciesService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  ngOnInit(): void {
    // ❗ Ensure we are in the browser
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const stored = localStorage.getItem('userLocation');

    if (stored) {
      const data = JSON.parse(stored);

      const isExpired = Date.now() - data.timestamp > 24 * 60 * 60 * 1000;

      if (!isExpired) {
        this.userLocation = [data.lat, data.lng];
        this.fetchPharmacies();
        this.showLocationDialog.set(false);
        return;
      } else {
        localStorage.removeItem('userLocation');
      }
    }

    // optional: show dialog if no valid location
    this.showLocationDialog.set(true);
  }

  @ViewChild('sortDropdown') sortDropdown!: ElementRef;
  @ViewChild('statusDropdown') statusDropdown!: ElementRef;

  searchIcon = Search;
  map_pin = MapPin;
  star = Star;
  chevronRight = ChevronRight;
  clock = Clock;
  navigate = Navigation;
  chevronDown = ChevronDown;
  check = Check;
  showLocationDialog = signal(true);
  base = Environment.base;
  // 🧩 Filter signal
  isLoading = false;
  search = signal('');
  sortBy = signal<'nearest' | 'rating'>('nearest');
  statusFilter = signal<'all' | 'open' | 'closed'>('all');
  currentPage: number = 0;
  userLocation!: [number, number];
  isSortOpen = signal(false);
  isStatusOpen = signal(false);
  totalPages = 0;

  pharmacies = signal<Content[]>([]);

  fetchPharmacies() {
    this.isLoading = true;
    const lat = this.userLocation?.[0];
    const lng = this.userLocation?.[1];

    this._pharmaciesService
      .getAllpharmacies(
        lat,
        lng,
        this.statusFilter(),
        this.search(),
        this.currentPage,
      )
      .subscribe({
        next: (res) => {
          
          this.pharmacies.set(res.data.content);
          this.totalPages = res.data.totalPages;
          console.log(this.totalPages);
          console.log(res);
          this.isLoading = false;
        },
        error: (err) => {
          this.pharmacies.set([]);
          console.log(err);
          this.isLoading = false;
        },
      });
  }

  selectedSortLabel = computed(
    () => this.sortOptions.find((o) => o.value === this.sortBy())?.label ?? '',
  );
  selectedStatusLabel = computed(
    () =>
      this.statusOptions.find((o) => o.value === this.statusFilter())?.label ??
      '',
  );

  sortOptions: SortOptions[] = [
    { label: 'Nearest', value: 'nearest' },
    { label: 'Highest Rating', value: 'rating' },
  ];

  statusOptions: StatusOptions[] = [
    { label: 'All', value: 'all' },
    { label: 'Open Now', value: 'open' },
  ];

  closeDialog() {
    this.showLocationDialog.set(false);

    localStorage.setItem(
      'locationDialogClosed',
      JSON.stringify({
        timestamp: Date.now(),
      }),
    );
  }

  filteredPharmacies = computed(() => {
    let list = [...this.pharmacies()];

    // 🔄 Sorting
    // if (this.sortBy() === 'nearest') {
    //   list.sort((a, b) => a.di - b.distance);
    // }

    // if (this.sortBy() === 'rating') {
    //   // list.sort((a, b) => b.rating - a.rating);
    // }

    return list;
  });

  onSearch(event: Event) {
    this.search.set((event.target as HTMLInputElement).value);
    this.fetchPharmacies();
  }

  onSortChange(event: Event) {
    this.sortBy.set((event.target as HTMLSelectElement).value as any);
  }

  onStatusChange(event: Event) {
    this.statusFilter.set((event.target as HTMLSelectElement).value as any);
  }

  toggleSortDropdown(event: MouseEvent) {
    event.stopPropagation();
    this.isSortOpen.update((v) => !v);
  }

  toggleStatusDropdown(event: MouseEvent) {
    event.stopPropagation(); // 🔥 IMPORTANT
    this.isStatusOpen.update((v) => !v);
  }
  selectSort(value: 'nearest' | 'rating') {
    this.sortBy.set(value);
    this.isSortOpen.set(false);
  }

  selectStatus(status: 'all' | 'open') {
    this.statusFilter.set(status);
    this.isStatusOpen.set(false);
    console.log('hamada');
    console.log(this.statusFilter());
    this.fetchPharmacies();
    console.log(this.pharmacies);
  }

  @HostListener('document:click', ['$event'])
  closeOutside(event: MouseEvent) {
    // const target = event.target as HTMLElement;

    // if (
    //   this.sortDropdown?.nativeElement.contains(target) ||
    //   this.statusDropdown?.nativeElement.contains(target)
    // ) {
    //   return;
    // }

    this.isSortOpen.set(false);
    this.isStatusOpen.set(false);
  }

  trackByValue(index: number, item: { value: string }) {
    return item.value;
  }

  getUserLocation() {
    // Close immediately
    this.closeDialog();

    // Let Angular render first
    setTimeout(() => {
      const stored = localStorage.getItem('userLocation');

      if (stored) {
        const data = JSON.parse(stored);

        const isExpired = Date.now() - data.timestamp > 24 * 60 * 60 * 1000;

        if (!isExpired) {
          const lat = data.lat;
          const lng = data.lng;

          this.userLocation = [lat, lng];
          this.fetchPharmacies();
          return;
        } else {
          localStorage.removeItem('userLocation');
        }
      }

      if (!navigator.geolocation) {
        alert('Geolocation is not supported by your browser');
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;

          localStorage.setItem(
            'userLocation',
            JSON.stringify({
              lat,
              lng,
              timestamp: Date.now(),
            }),
          );

          this.userLocation = [lat, lng];
          this.fetchPharmacies();
        },
        (error) => {
          console.error('Geolocation error:', error);
        },
      );
    }, 6);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    console.log(page);
    this.fetchPharmacies();
  }
}
