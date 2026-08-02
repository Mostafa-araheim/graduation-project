import { Environment } from './../../../Environment/environment';
import { Component, OnDestroy, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import {
  LucideAngularModule,
  Upload,
  Image,
  CheckCircle,
  Loader2,
  MapPin,
  Star,
  Clock,
  ChevronRight,
  Navigation,
} from 'lucide-angular';

import { CartService } from '../../services/cart/cart.service';
import { Router } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

type Step = 'upload' | 'processing' | 'results';

interface Pharmacy {
  id: string;
  name: string;

  image: string;

  distance: string;

  rating: number;

  availability: string;

  price: number;

  estimatedTime: string;

  bestChoice: boolean;

  medicines: {
    pharmacyProductId: number;
    quantity: number;
  }[];
}

@Component({
  selector: 'app-prescription',
  standalone: true,
  imports: [CommonModule, HttpClientModule, LucideAngularModule, ToastModule],
  providers: [MessageService],
  templateUrl: './prescription.component.html',
  styleUrl: './prescription.component.css',
})
export class PrescriptionComponent implements OnInit, OnDestroy {
  // ================= ICONS =================
  upload = Upload;
  image = Image;
  check = CheckCircle;
  loader = Loader2;
  mapPin = MapPin;
  star = Star;
  clock = Clock;
  chevronRight = ChevronRight;
  navigate = Navigation;

  // ================= SERVICES =================
  private http = inject(HttpClient);
  private cartService = inject(CartService);
  private router = inject(Router);
  private messageService = inject(MessageService);
  // ================= API =================
  apiUrl = Environment.base + '/api/v1/scan-prescription/scan/nearby';

  // ================= STATE =================
  step = signal<Step>('upload');
  progress = signal(0);
  previewUrl = signal<string | null>(null);
  showLocationDialog = signal(false);
  base = Environment.base;
  userLatitude: number | null = null;
  userLongitude: number | null = null;
  orderState = signal<Record<string, { loading: boolean; success: boolean }>>(
    {},
  );
  selectedFile: File | null = null;
  private processingInterval: any;
  scannedMedicines: any[] = [];
  nearbyPharmacies: Pharmacy[] = [];
  errorMessage = signal<string | null>(null);

  // ================= INIT =================
  ngOnInit(): void {
    const location = this.getStoredLocation();

    if (location) {
      this.userLatitude = location.lat;
      this.userLongitude = location.lng;
      this.showLocationDialog.set(false);
    } else {
      this.showLocationDialog.set(true);
    }
  }

  // ================= LOCATION =================
  getStoredLocation() {
    const stored = localStorage.getItem('userLocation');
    if (!stored) return null;

    try {
      return JSON.parse(stored);
    } catch {
      return null;
    }
  }

  getOrderState(pharmacyId: string) {
    return this.orderState()[pharmacyId] || { loading: false, success: false };
  }

  getUserLocation() {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const location = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
          timestamp: Date.now(),
        };

        localStorage.setItem('userLocation', JSON.stringify(location));

        this.userLatitude = location.lat;
        this.userLongitude = location.lng;

        this.showLocationDialog.set(false);
      },
      (error) => console.error('Location error:', error),
    );
  }

  closeDialog() {
    this.showLocationDialog.set(false);
  }

  // ================= FILE =================
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.errorMessage.set(null);

    this.selectedFile = file;

    this.revokePreview();
    this.previewUrl.set(URL.createObjectURL(file));
  }

  removePreview() {
    this.revokePreview();
    this.previewUrl.set(null);
    this.selectedFile = null;
  }

  // ================= PROCESS =================
  startProcessing() {
    if (!this.previewUrl()) return;

    this.step.set('processing');
    this.progress.set(0);

    clearInterval(this.processingInterval);

    this.processingInterval = setInterval(() => {
      const next = Math.min(this.progress() + 10, 100);
      this.progress.set(next);

      if (next >= 100) {
        clearInterval(this.processingInterval);

        // 👇 CALL API HERE
        this.scanNearbyPharmacies();
      }
    }, 250);
  }

  // ================= API CALL =================
  scanNearbyPharmacies() {
    if (!this.selectedFile || !this.userLatitude || !this.userLongitude) {
      this.errorMessage.set(
        'Please upload a prescription and enable location services.',
      );

      this.step.set('upload');
      return;
    }

    const formData = new FormData();

    formData.append('file', this.selectedFile);
    formData.append('userLatitude', this.userLatitude.toString());
    formData.append('userLongitude', this.userLongitude.toString());

    this.http.post<any>(this.apiUrl, formData).subscribe({
      next: (res) => {
        console.log(res);

        this.errorMessage.set(null);

        if (res.success) {
          this.scannedMedicines = res.data.scanned_medicines;

          this.nearbyPharmacies = res.data.nearby_pharmacies.map(
            (p: any, index: number) => ({
              id: p.pharmacy_id.toString(),
              name: p.pharmacy_name,

              image: p.pharmacy_image,

              distance: `${p.distance_km.toFixed(1)} km`,

              rating: p.average_rating,

              availability: `${p.available_count} of ${p.total_medicines_requested} medicines available`,

              price: p.total_price,

              estimatedTime: p.is_open ? 'Open now' : 'Closed',

              bestChoice: index === 0,

              medicines: p.available_medicines.map((m: any) => ({
                pharmacyProductId: m.pharmacy_product_id,
                quantity: 1,
              })),
            }),
          );

          this.step.set('results');
        } else {
          this.errorMessage.set(
            res.message || 'Failed to process prescription.',
          );

          this.step.set('upload');
        }
      },

      error: (err) => {
        console.error('API error:', err);

        this.errorMessage.set(
          err?.error?.message ||
            'Something went wrong while processing your prescription. Please try again.',
        );

        this.progress.set(0);
        this.step.set('upload');
      },
    });
  }

  // ================= RESET =================
  resetToUpload() {
    clearInterval(this.processingInterval);
    this.progress.set(0);
    this.revokePreview();
    this.previewUrl.set(null);
    this.step.set('upload');
  }

  // ================= CLEANUP =================
  private revokePreview() {
    const url = this.previewUrl();
    if (url?.startsWith('blob:')) URL.revokeObjectURL(url);
  }

  ngOnDestroy(): void {
    clearInterval(this.processingInterval);
    this.revokePreview();
  }

  // ================= UI HELPERS =================
  steps: Step[] = ['upload', 'processing', 'results'];
  labels = ['Upload', 'Processing', 'Results'];

  isPast(index: number) {
    return this.steps.indexOf(this.step()) > index;
  }

  isActive(key: Step) {
    return this.step() === key;
  }

  orderNow(pharmacy: Pharmacy) {
    const current = this.orderState();

    // prevent double click spam
    if (current[pharmacy.id]?.loading) return;

    // set loading state
    this.orderState.set({
      ...current,
      [pharmacy.id]: { loading: true, success: false },
    });

    const request = [
      {
        items: pharmacy.medicines,
      },
    ];

    this.cartService.assignPrescriptionToUser(request).subscribe({
      next: () => {
        this.orderState.set({
          ...this.orderState(),
          [pharmacy.id]: {
            loading: false,
            success: true,
          },
        });

        this.messageService.add({
          severity: 'success',
          summary: 'Added to Cart',
          detail: `${pharmacy.name} added successfully`,
        });
      },

      error: (err) => {
        this.orderState.set({
          ...this.orderState(),
          [pharmacy.id]: {
            loading: false,
            success: false,
          },
        });

        this.messageService.add({
          severity: 'error',
          summary: 'Failed',
          detail: err?.error?.message || 'Something went wrong',
        });
      },
    });
  }
}
