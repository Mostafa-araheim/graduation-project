import {
  Component,
  Input,
  ViewChild,
  ElementRef,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  Inject,
  PLATFORM_ID,
  signal,
  AfterViewInit,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { PharmaciesService } from '../../Core/services/Pharmacies/pharmacies.service';
import { RouterLink } from '@angular/router';

interface Pharmacy {
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

@Component({
  selector: 'app-pharmacy-map',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pharmacies-map.component.html',
  styleUrl: './pharmacies-map.component.css',
})
export class PharmacyMapComponent
  implements OnChanges, OnDestroy, AfterViewInit
{
  @ViewChild('mapRef', { static: false }) mapRef!: ElementRef<HTMLDivElement>;

  @Input() center: [number, number] = [25, 27];
  @Input() userLocation?: [number, number];

  private map!: L.Map;
  private markers: L.Marker[] = [];
  private userIcon!: any;

  L!: typeof import('leaflet');

  pharmacies = signal<Pharmacy[]>([]);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private _pharmaciesService: PharmaciesService,
  ) {}

  async ngAfterViewInit() {
    // Load pharmacies
    this._pharmaciesService.getPharmaciesLocation().subscribe({
      next: (res) => {
        this.pharmacies.set(res.data);
        this.updateMarkers();
      },
    });

    if (isPlatformBrowser(this.platformId)) {
      this.L = await import('leaflet');

      delete (this.L.Icon.Default.prototype as any)._getIconUrl;

      this.L.Icon.Default.mergeOptions({
        iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
        iconUrl: 'assets/leaflet/marker-icon.png',
        shadowUrl: 'assets/leaflet/marker-shadow.png',
      });

      this.userIcon = this.L.divIcon({
        html: `
        <div style="
          width:18px;
          height:18px;
          background:#3b82f6;
          border:3px solid #fff;
          border-radius:50%;
          box-shadow:0 0 8px rgba(59,130,246,0.5);
        "></div>
      `,
        className: '',
        iconSize: [18, 18],
        iconAnchor: [9, 9],
      });

      this.initMap();

      // Load user location from localStorage
      this.loadUserLocationFromStorage();

      this.updateMarkers();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.map) return;

    if (changes['userLocation'] && this.userLocation) {
      this.map.setView(this.userLocation, 14);
    }

    if (changes['pharmacies'] || changes['userLocation']) {
      this.updateMarkers();
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    if (this.map) {
      this.map.remove();
    }

    this.map = this.L.map(this.mapRef.nativeElement).setView(this.center, 5);

    this.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap',
    }).addTo(this.map);

    this.updateMarkers();
  }

  private loadUserLocationFromStorage() {
    const stored = localStorage.getItem('userLocation');

    if (!stored) return;

    const data = JSON.parse(stored);

    const isExpired = Date.now() - data.timestamp > 24 * 60 * 60 * 1000;

    if (isExpired) {
      localStorage.removeItem('userLocation');
      return;
    }

    this.userLocation = [data.lat, data.lng];

    if (this.map) {
      this.map.setView(this.userLocation, 14);
    }
  }

  private clearMarkers(): void {
    this.markers.forEach((marker) => marker.remove());
    this.markers = [];
  }

  private updateMarkers(): void {
    if (!this.map) return;

    this.clearMarkers();

    // User marker
    if (this.userLocation) {
      const userMarker = this.L.marker(this.userLocation, {
        icon: this.userIcon,
      })
        .addTo(this.map)
        .bindPopup(`<b>📍 You are here</b>`);

      this.markers.push(userMarker);
    }

    // Pharmacy markers
    this.pharmacies().forEach((pharmacy) => {
      const marker = this.L.marker([
        pharmacy.latitude,
        pharmacy.longitude,
      ]).addTo(this.map);

      const statusColor = pharmacy.is_closed ? '#9ca3af' : '#16a34a';

      const statusText = pharmacy.is_closed
        ? `Open until ${pharmacy.closing_time}`
        : pharmacy.closing_time;

      marker.bindPopup(`
        <div style="min-width:200px;font-family:system-ui;">
          <h3 style="font-weight:700;font-size:15px;margin:0 0 6px;">
            ${pharmacy.name}
          </h3>
          <p style="font-size:13px;color:#6b7280;margin:0 0 4px;">
            📍 ${pharmacy.address}
          </p>
          <p style="font-size:13px;margin:0 0 4px;">
            ⭐ ${pharmacy.rating} (${pharmacy.review_count} reviews)
          </p>
          <p style="font-size:13px;color:${statusColor};margin:0 0 8px;">
            🕐 ${statusText}
          </p>
          <a href="/pharmacyDetails/${pharmacy.pharmacy_id}"
            style="display:block;text-align:center;padding:6px 12px;
                   background:hsl(174,62%,40%);
                   color:#fff;border-radius:6px;
                   text-decoration:none;font-size:13px;font-weight:600;">
            View Pharmacy
          </a>
        </div>
      `);

      this.markers.push(marker);
    });

    // Auto zoom if no user location
    if (!this.userLocation && this.pharmacies().length > 0) {
      const first = this.pharmacies()[0];
      this.map.setView([first.latitude, first.longitude], 12);
    }
  }
}
