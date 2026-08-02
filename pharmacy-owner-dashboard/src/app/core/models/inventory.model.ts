import { AvailabilityStatus, DosageForm } from './enums.model';

export interface PharmacyProductDto {
  pharmacy_product_id: number;
  pharmacy_name: string;
  product_id: number;
  product_name: string;
  description: string;
  product_image: string;
  price: number;
  quantity: number;
  in_stock: boolean;
  requires_prescription: boolean;
  dosage_form: DosageForm;
  strength: string;
  manufacturer: string;
  category_id: number;
  category_name: string;
  brand_id: number;
  brand_name: string;
}

export interface AddPharmacyProductRequest {
  product_id: number;
  quantity: number;
  price: number;
}

export interface UpdatePharmacyProductRequest {
  quantity?: number;
  price?: number;
}

export interface ProductResponse {
  id: number;
  name: string;
  description: string;
  requiresPrescription: boolean;
  dosageForm: DosageForm;
  strength: string;
  manufacturer: string;
  category: string;
}

export interface InventoryFilterParams {
  productName?: string;
  availabilityStatus?: AvailabilityStatus;
  categoryName?: string;
  minPrice?: number;
  maxPrice?: number;
}

export interface ProductSearchParams {
  productName?: string;
  categoryName?: string;
  dosageForm?: DosageForm;
}
