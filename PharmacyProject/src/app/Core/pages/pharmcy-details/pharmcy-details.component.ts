import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ProductCardComponent } from '../../../Shared/product-card/product-card.component';
import {
  LucideAngularModule,
  MapPin,
  Star,
  Heart,
  Share2,
  Clock,
  Phone,
  Globe,
  ArrowLeft,
} from 'lucide-angular';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PharmaciesService } from '../../services/Pharmacies/pharmacies.service';
import { LoaderComponent } from '../../../Shared/loader/loader.component';
import { Environment } from '../../../Environment/environment';
import { CartService } from '../../services/cart/cart.service';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

export interface Root {
  success: boolean;
  message: string;
  data: Data;
  error: any;
  timestamp: string;
}

export interface Data {
  categories: Category[];
  pharmacy_address: PharmacyAddress;
  pharmacy_dto: PharmacyDto;
  reviews: Review[];
}

export interface Category {
  categoryId: number;
  categoryName: string;
  imageUrl: string;
}

export interface PharmacyAddress {
  apartmentNumber: any;
  city: string;
  country: string;
  pharmacyId: number;
  postalCode: string;
  street: string;
}

export interface PharmacyDto {
  pharmacy_id: number;
  name: string;
  image_url: string;
  rating: number;
  distance_in_kilometers: any;
  opening_time: string;
  closing_time: string;
  latitude: number;
  longitude: number;
  review_count: number;
  is_closed: boolean;
  address: string;
}

export interface Review {
  name: string;
  comment: string;
}

export interface Products {
  success: boolean;
  message: string;
  data: Data;
  error: any;
  timestamp: string;
}

export interface ProductsData {
  content: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

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
  selector: 'app-pharmcy-details',
  standalone: true,
  imports: [
    CommonModule,
    ProductCardComponent,
    LucideAngularModule,
    RouterLink,
    DatePipe,
    LoaderComponent,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './pharmcy-details.component.html',
  styleUrl: './pharmcy-details.component.css',
})
export class PharmcyDetailsComponent implements OnInit {
  ngOnInit(): void {
    this.getId();
    // this.getPharmacyDetails(this.pharmacyId);
  }

  constructor(
    private _ActivatedRoute: ActivatedRoute,
    private _pharmacyServices: PharmaciesService,
    private _cartService: CartService,
    private _messageService: MessageService,
  ) {}

  mapPin = MapPin;
  star = Star;
  heart = Heart;
  share = Share2;
  clock = Clock;
  phone = Phone;
  globe = Globe;
  arrowLeft = ArrowLeft;
  //=============================
  baseUrl = Environment.base;
  isloading = false;
  isProductLoading = false;
  activeTab: 'products' | 'info' | 'reviews' = 'products';
  selectedCategory: string | null = null;
  pharmacyId!: string;
  categories: Category[] = [];
  pharmacyData!: PharmacyDto;
  pharmacyAddres!: PharmacyAddress;
  reviews: Review[] = [];
  products: Product[] = [];

  getPharmacyDetails(id: string) {
    this.isloading = true;
    this._ActivatedRoute.params.subscribe((res) => {
      this.pharmacyId = res['id'];
    });

    this._pharmacyServices.getPharmacyDetails(id).subscribe({
      next: (res) => {
        console.log(res);
        this.pharmacyData = res.data.pharmacy_dto;
        this.pharmacyAddres = res.data.pharmacy_address;
        this.reviews = res.data.reviews;
        this.categories = res.data.categories;
        this.isloading = false;
      },
      error: (err) => {
        this.isloading = false;
      },
    });
  }

  getProduct(pharmacyId: string, categoryId: string) {
    this.isProductLoading = true;
    this._pharmacyServices
      .getProductByPharmcyIdAndAcategoryId(pharmacyId, categoryId)
      .subscribe({
        next: (res) => {
          this.products = res.data.content;
          console.log(res);
          this.isProductLoading = false;
        },
        error: (err) => {
          this.isProductLoading = false;
        },
      });
  }

  getId() {
    this._ActivatedRoute.params.subscribe((res) => {
      this.pharmacyId = res['id'];

      this.getPharmacyDetails(this.pharmacyId);
    });
  }

  getInitials(name: string): string {
    if (!name) return '';

    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }
  // get filteredProducts(): Product[] {
  //   if (!this.selectedCategory) return [];
  //   return this.products.filter((p) => p.category === this.selectedCategory);
  // }

  selectCategory(id: string, name: string) {
    this.selectedCategory = name;
    this.getProduct(this.pharmacyId, id);
    this.activeTab = 'products';
  }

  backToCategories() {
    this.selectedCategory = null;
  }

  backToPharmacies() {
    alert('Navigate to /pharmacies');
  }

  setTab(tab: 'products' | 'info' | 'reviews') {
    this.activeTab = tab;
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
