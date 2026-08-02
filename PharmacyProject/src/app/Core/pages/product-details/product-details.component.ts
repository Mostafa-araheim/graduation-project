import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  LucideAngularModule,
  Plus,
  Minus,
  Star,
  ChevronRight,
  ChevronLeft,
} from 'lucide-angular';

import { ProductService } from '../../services/Product/product.service';
import { Environment } from '../../../Environment/environment';
import { LoaderComponent } from '../../../Shared/loader/loader.component';
import { CartService } from '../../services/cart/cart.service';
import { log } from 'console';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

export interface Data {
  success: boolean;
  message: string;
  data: Product;
  error: any;
  timestamp: string;
}

// export interface Product {
//   pharmacy_product_id: number;
//   pharmacy_name: string;
//   product_id: number;
//   product_name: string;
//   description: string;
//   product_image: any;
//   price: number;
//   quantity: number;
//   in_stock: boolean;
//   requires_prescription: boolean;
//   dosage_form: string;
//   strength: string;
//   manufacturer: string;
//   category_id: number;
//   category_name: string;
//   brand_id: number;
//   brand_name: string;
// }
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
  selector: 'app-product-details',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, LoaderComponent, ToastModule],
  providers: [MessageService],
  templateUrl: './product-details.component.html',
  styleUrl: './product-details.component.css',
})
export class ProductDetailsComponent implements OnInit {
  plus = Plus;
  minus = Minus;
  star = Star;
  chevronRight = ChevronRight;
  chevronLeft = ChevronLeft;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private _messageService: MessageService,
    private _cartService: CartService,
  ) {}

  product = signal<Product | null>(null);

  baseurl = Environment.base;

  loading = signal(false);
  error = signal<string | null>(null);

  selectedImage = 0;
  quantity = 1;
  isWishlisted = false;
  activeTab = 'description';

  productImages = [
    'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&auto=format',
    'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=800&auto=format',
    'https://images.unsplash.com/photo-1550572017-4fcdbb59cc32?w=800&auto=format',
  ];

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));

      if (id) {
        this.loadProduct(id);
      }
    });
  }

  loadProduct(id: number): void {
    this.loading.set(true);
    this.error.set(null);

    this.productService.getProductById(id).subscribe({
      next: (res) => {
        console.log(res);
        this.product.set(res.data);

        // Use API image as first image
        if (res.data.product_image) {
          this.productImages = [res.data.product_image];
        }

        this.loading.set(false);
      },

      error: (err) => {
        console.error(err);

        this.error.set('Failed to load product');
        this.loading.set(false);
      },
    });
  }

  nextImage(): void {
    this.selectedImage =
      this.selectedImage === this.productImages.length - 1
        ? 0
        : this.selectedImage + 1;
  }

  prevImage(): void {
    this.selectedImage =
      this.selectedImage === 0
        ? this.productImages.length - 1
        : this.selectedImage - 1;
  }

  setImage(index: number): void {
    this.selectedImage = index;
  }

  increaseQty(): void {
    this.quantity++;
  }

  decreaseQty(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  toggleWishlist(): void {
    this.isWishlisted = !this.isWishlisted;
  }

  setTab(tab: string): void {
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
