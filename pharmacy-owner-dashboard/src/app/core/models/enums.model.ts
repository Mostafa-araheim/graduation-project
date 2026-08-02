export type DosageForm =
  | 'Tablet' | 'Capsule' | 'Syrup' | 'Suspension'
  | 'Injection' | 'Cream' | 'Ointment' | 'Inhaler'
  | 'Suppository' | 'Patch' | 'Drops';

export type AvailabilityStatus = 'Available' | 'OutOfStock' | 'LimitedSupply';

export type DeliveryType = 'PICKUP' | 'DELIVERY';

export type PaymentMethod = 'CARD' | 'CASH';

export type OrderStatus =
  | 'PENDING_PAYMENT' | 'PLACED' | 'CONFIRMED'
  | 'FAILED' | 'CANCELED';

export type AnalyticsPeriod = 'week' | 'month' | 'year';

export type UserRole = 'ROLE_CUSTOMER' | 'ROLE_OWNER' | 'ROLE_PHARMACIST' | 'ROLE_ADMIN';

export const DOSAGE_FORMS: DosageForm[] = [
  'Tablet', 'Capsule', 'Syrup', 'Suspension', 'Injection',
  'Cream', 'Ointment', 'Inhaler', 'Suppository', 'Patch', 'Drops'
];

export const AVAILABILITY_STATUSES: AvailabilityStatus[] = [
  'Available', 'OutOfStock', 'LimitedSupply'
];

export const ORDER_STATUSES: OrderStatus[] = [
  'PENDING_PAYMENT', 'PLACED', 'CONFIRMED', 'FAILED', 'CANCELED'
];
