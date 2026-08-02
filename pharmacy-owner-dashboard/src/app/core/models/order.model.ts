import { DeliveryType, OrderStatus, PaymentMethod } from './enums.model';

export interface OwnerOrderResponse {
  orderId: number;
  customerId: number;
  customerName: string;
  pharmacyId: number;
  totalPrice: number;
  deliveryType: DeliveryType;
  paymentMethod: PaymentMethod;
  status: OrderStatus;
  items: OrderItem[];
}

export interface OrderItem {
  orderItemId: number;
  productId: number;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
  subtotal: number;
}
