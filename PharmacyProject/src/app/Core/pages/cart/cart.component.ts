import { Component, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Trash2,
  Plus,
  Minus,
  Truck,
  ChevronRight,
  Shield,
  ArrowLeft,
  Tag,
} from 'lucide-angular';
import { RouterLink } from '@angular/router';
import { CartService, Cart } from '../../services/cart/cart.service';
import { ProfileService } from '../../services/profile/profile.service';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';

export interface Address {
  addressId: number;
  street: string;
  city: string;
  postalcode: string;
  country: string;
  apartmentNumber: string;
}

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideAngularModule,
    RouterLink,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
  ],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css',
})
export class CartComponent {
  // icons
  trash = Trash2;
  plus = Plus;
  minus = Minus;
  truck = Truck;
  chevronRight = ChevronRight;
  shield = Shield;
  arrowLeft = ArrowLeft;
  tag = Tag;

  // 🔥 reactive cart
  cartItems = this.cartService.carts$;

  selectedPharmacy = signal<string | null>(null);

  promoCodes: Record<string, string> = {};
  promoApplied: Record<string, boolean> = {};

  showCheckoutModal = false;

  checkoutData = {
    deliveryType: 'PICKUP',
    paymentMethod: 'CASH',
    deliveryAddressId: null as number | null,
  };

  adresses = signal<Address[]>([]);

  constructor(
    private cartService: CartService,
    private _profileService: ProfileService,
  ) {
    // 🔥 auto select first cart
    effect(
      () => {
        const carts = this.cartItems();

        if (carts.length > 0 && !this.selectedPharmacy()) {
          this.selectedPharmacy.set(carts[0].cartName);
        }
      },
      { allowSignalWrites: true },
    );
  }

  ngOnInit() {
    this.getUserAddress();
  }

  // =========================
  // 🔹 UI HELPERS
  // =========================

  pharmacies = computed(() => this.cartItems().map((c) => c.cartName));

  setSelectedPharmacy(pharmacy: string) {
    this.selectedPharmacy.set(pharmacy);
  }

  // =========================
  // 🔹 CART ACTIONS
  // =========================

  updateQuantity(cartId: number, productId: number, delta: number) {
    if (delta > 0) {
      this.cartService.incrementItem(cartId, productId).subscribe();
    } else {
      this.cartService.decreaseItem(cartId, productId).subscribe();
    }
  }

  removeItem(cartId: number, productId: number) {
    this.cartService.removeItem(cartId, productId).subscribe();
  }

  // =========================
  // 🔹 TOTALS
  // =========================

  getPharmacyTotals(pharmacy: string) {
    const cart = this.cartItems().find((c) => c.cartName === pharmacy);
    if (!cart) return null;

    const subtotal = cart.totalPrice;

    const deliveryFee = subtotal > 50 ? 0 : 4.99;
    const applied = this.promoApplied[pharmacy] || false;
    const promoDiscount = applied ? subtotal * 0.1 : 0;
    const total = subtotal + deliveryFee - promoDiscount;

    return {
      items: cart.items,
      subtotal,
      deliveryFee,
      promoDiscount,
      total,
      applied,
    };
  }

  summaryData = computed(() => {
    const pharmacy = this.selectedPharmacy();
    return pharmacy ? this.getPharmacyTotals(pharmacy) : null;
  });

  // =========================
  // 🔹 PROMO
  // =========================

  applyPromoCode(pharmacy: string) {
    const code = this.promoCodes[pharmacy] || '';

    if (code.toLowerCase() === 'save10') {
      this.promoApplied = { ...this.promoApplied, [pharmacy]: true };
    } else {
      alert('Invalid promo code. Try "SAVE10"');
    }
  }

  // =========================
  // 🔹 CHECKOUT
  // =========================

  confirmCheckout(): void {
    if (
      this.checkoutData.deliveryType === 'DELIVERY' &&
      !this.checkoutData.deliveryAddressId
    ) {
      alert('Please select a delivery address');
      return;
    }

    const selectedCart = this.cartItems().find(
      (x) => x.cartName === this.selectedPharmacy(),
    );

    console.log(selectedCart?.cartId);
    console.log(this.checkoutData.deliveryType);
    console.log(this.checkoutData.paymentMethod);
    // console.log()

    if (!selectedCart) {
      return;
    }

    this.cartService
      .checkout(
        selectedCart.cartId,
        this.checkoutData.deliveryType,
        this.checkoutData.paymentMethod,
        this.checkoutData.deliveryAddressId!,
      )
      .subscribe({
        next: (res) => {
          this.showCheckoutModal = false;

          // Redirect to Stripe Checkout
          window.location.href = res.data.checkoutUrl;
        },
      });
  }

  getUserAddress() {
    this._profileService.getUserAddresess().subscribe({
      next: (res) => {
        this.adresses.set(res.data);
      },
    });
  }
}
