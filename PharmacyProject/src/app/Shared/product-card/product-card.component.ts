import { Component, EventEmitter, input, Output } from '@angular/core';
import { LucideAngularModule, Heart, ShoppingCart, Plus } from 'lucide-angular';
import { CartService } from '../../Core/services/cart/cart.service';
import { Environment } from '../../Environment/environment';
import { RouterLink } from '@angular/router';

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
  selector: 'app-product-card',
  standalone: true,
  imports: [LucideAngularModule, RouterLink],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css',
})
export class ProductCardComponent {
  /**
   *
   */
  base = Environment.base;
  heart = Heart;
  plus = Plus;
  shoppingCart = ShoppingCart;

  productData = input.required<Product>();
  @Output() add = new EventEmitter<Product>();

  OnAddClick() {
    this.add.emit(this.productData());
  }
}
