import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { switchMap, tap, map, catchError } from 'rxjs/operators';
import { Environment } from '../../../Environment/environment';
import { AuthService } from '../auth/auth.service';
import { Product } from '../../pages/medicines/medicines.component';

export interface Cart {
  cartId: number;
  cartName: string;
  items: CartItem[];
  totalItems: number;
  totalPrice: number;
  updatedAt: string;
}

export interface CartItem {
  pharmacyProductId: number;
  productName: string;
  productImage: string;
  quantity: number;
  pricePerUnit: number;
  totalPrice: number;
}

@Injectable({
  providedIn: 'root',
})
export class CartService {
  constructor(
    private _httpClient: HttpClient,
    private _authService: AuthService,
  ) {
    this.initCart();
  }

  // =========================
  // STATE
  // =========================
  private carts = signal<Cart[]>([]);

  cartCount = computed(() =>
    this.carts().reduce((sum, cart) => sum + cart.totalItems, 0),
  );

  get carts$() {
    return this.carts;
  }

  // =========================
  // SAFE CHECK
  // =========================
  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  private isLoggedIn(): boolean {
    return this.isBrowser() && !!localStorage.getItem('PharmacyAccessToken');
  }

  // =========================
  // INIT
  // =========================
  private initCart() {
    if (!this.isBrowser()) return;

    if (this._authService.isLogin()) {
      this.GetUserCart().subscribe((res) => {
        this.carts.set(res.data);
      });
    } else {
      const stored = localStorage.getItem('guest_carts');
      this.carts.set(stored ? JSON.parse(stored) : []);
    }
  }

  // =========================
  // LOCAL STORAGE
  // =========================
  private saveLocal(carts: Cart[]) {
    if (!this.isBrowser()) return;

    localStorage.setItem('guest_carts', JSON.stringify(carts));
    this.carts.set([...carts]);
  }

  private addToLocalCart(product: Product) {
    const carts = [...this.carts()];

    let cart = carts.find((c) => c.cartId === product.pharmacy_id);

    if (!cart) {
      cart = {
        cartId: product.pharmacy_id,
        cartName: product.pharmacy_name,
        items: [],
        totalItems: 0,
        totalPrice: 0,
        updatedAt: new Date().toISOString(),
      };
      carts.push(cart);
    }

    let item = cart.items.find(
      (i) => i.pharmacyProductId === product.pharmacy_product_id,
    );

    if (item) {
      item.quantity++;
      item.totalPrice = item.quantity * item.pricePerUnit;
    } else {
      cart.items.push({
        pharmacyProductId: product.pharmacy_product_id,
        productName: product.product_name,
        productImage: product.product_image,
        quantity: 1,
        pricePerUnit: product.price,
        totalPrice: product.price,
      });
    }

    cart.totalItems = cart.items.reduce((s, i) => s + i.quantity, 0);
    cart.totalPrice = cart.items.reduce((s, i) => s + i.totalPrice, 0);
    cart.updatedAt = new Date().toISOString();

    this.saveLocal(carts);
  }

  // =========================
  // PUBLIC ADD TO CART
  // =========================
  addToCart(product: Product): Observable<void> {
    if (!this._authService.isLogin()) {
      this.addToLocalCart(product);
      return of(void 0);
    }

    return this._httpClient
      .post(Environment.base_url + '/carts/items', {
        pharmacyProductId: product.pharmacy_product_id,
      })
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
        map(() => void 0),
      );
  }

  // =========================
  // GET CART
  // =========================
  GetUserCart(): Observable<any> {
    return this._httpClient.get(Environment.base_url + '/carts/user');
  }

  // =========================
  // SYNC GUEST CART
  // =========================
  assignAnonymousCartToUser(): Observable<any> {
    if (!this.isBrowser()) return this.GetUserCart();

    const stored = localStorage.getItem('guest_carts');

    if (!stored) {
      return this.GetUserCart();
    }

    let carts: Cart[];

    try {
      carts = JSON.parse(stored);
    } catch {
      localStorage.removeItem('guest_carts');
      return this.GetUserCart();
    }

    const request = carts.map((cart) => ({
      items: cart.items.map((item) => ({
        pharmacyProductId: item.pharmacyProductId,
        quantity: item.quantity,
      })),
    }));

    if (request.length === 0) {
      localStorage.removeItem('guest_carts');
      return this.GetUserCart();
    }

    return this._httpClient
      .post(Environment.base_url + '/carts/assign', request)
      .pipe(
        tap(() => localStorage.removeItem('guest_carts')),
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
        catchError(() => this.GetUserCart()),
      );
  }

  assignPrescriptionToUser(
    request: {
      items: {
        pharmacyProductId: number;
        quantity: number;
      }[];
    }[],
  ): Observable<any> {
    return this._httpClient
      .post(Environment.base_url + '/carts/assign', request)
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => {
          if (res?.data) {
            this.carts.set(res.data);
          }
        }),
      );
  }
  // =========================
  // CART ACTIONS (API)
  // =========================
  deleteItem(cartId: number, pharmacyProductId: number) {
    return this._httpClient
      .delete(
        Environment.base_url + `/carts/${cartId}/items/${pharmacyProductId}`,
      )
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
      );
  }

  increment(cartId: number, pharmacyProductId: number) {
    return this._httpClient
      .post(Environment.base_url + `/carts/${cartId}/items/increase`, {
        pharmacyProductId,
      })
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
      );
  }

  decrease(cartId: number, pharmacyProductId: number) {
    return this._httpClient
      .post(Environment.base_url + `/carts/${cartId}/items/decrease`, {
        pharmacyProductId,
      })
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
      );
  }

  clearCart(cartId: number) {
    return this._httpClient
      .delete(Environment.base_url + `/carts/${cartId}/clear`)
      .pipe(
        switchMap(() => this.GetUserCart()),
        tap((res) => this.carts.set(res.data)),
      );
  }

  // =========================
  // LOCAL ACTIONS
  // =========================
  incrementLocal(cartId: number, productId: number) {
    const carts = [...this.carts()];
    const cart = carts.find((c) => c.cartId === cartId);
    if (!cart) return;

    const item = cart.items.find((i) => i.pharmacyProductId === productId);
    if (!item) return;

    item.quantity++;
    item.totalPrice = item.quantity * item.pricePerUnit;

    cart.totalItems = cart.items.reduce((s, i) => s + i.quantity, 0);
    cart.totalPrice = cart.items.reduce((s, i) => s + i.totalPrice, 0);

    this.saveLocal(carts);
  }

  decreaseLocal(cartId: number, productId: number) {
    const carts = [...this.carts()];
    const cart = carts.find((c) => c.cartId === cartId);
    if (!cart) return;

    const item = cart.items.find((i) => i.pharmacyProductId === productId);
    if (!item) return;

    item.quantity = Math.max(1, item.quantity - 1);
    item.totalPrice = item.quantity * item.pricePerUnit;

    cart.totalItems = cart.items.reduce((s, i) => s + i.quantity, 0);
    cart.totalPrice = cart.items.reduce((s, i) => s + i.totalPrice, 0);

    this.saveLocal(carts);
  }

  deleteItemLocal(cartId: number, productId: number) {
    let carts = [...this.carts()];

    carts = carts
      .map((cart) => {
        if (cart.cartId !== cartId) return cart;

        const items = cart.items.filter(
          (i) => i.pharmacyProductId !== productId,
        );

        return {
          ...cart,
          items,
          totalItems: items.reduce((s, i) => s + i.quantity, 0),
          totalPrice: items.reduce((s, i) => i.totalPrice, 0),
        };
      })
      .filter((cart) => cart.items.length > 0);

    this.saveLocal(carts);
  }

  // =========================
  // WRAPPERS
  // =========================
  incrementItem(cartId: number, productId: number): Observable<any> {
    if (!this.isLoggedIn()) {
      this.incrementLocal(cartId, productId);
      return of(null);
    }

    return this.increment(cartId, productId);
  }

  decreaseItem(cartId: number, productId: number): Observable<any> {
    if (!this.isLoggedIn()) {
      this.decreaseLocal(cartId, productId);
      return of(null);
    }

    return this.decrease(cartId, productId);
  }

  removeItem(cartId: number, productId: number): Observable<any> {
    if (!this.isLoggedIn()) {
      this.deleteItemLocal(cartId, productId);
      return of(null);
    }

    return this.deleteItem(cartId, productId);
  }

  // =========================
  // CHECKOUT
  // =========================
  checkout(
    cartId: number,
    deliveryType: string,
    paymentMethod: string,
    deliveryAddressId: number,
  ): Observable<any> {
    return this._httpClient.post(
      Environment.base_url + `/carts/${cartId}/checkout`,
      {
        deliveryType,
        paymentMethod,
        deliveryAddressId,
      },
    );
  }
}
