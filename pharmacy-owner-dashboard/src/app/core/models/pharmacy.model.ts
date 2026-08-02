export interface PharmacyDto {
  pharmacy_id: number;
  name: string;
  image_url: string;
  rating: number;
  distance_in_kilometers: number | null;
  opening_time: string;
  closing_time: string;
  latitude: number;
  longitude: number;
  review_count: number;
  is_closed: boolean;
  address: string;
}
export interface PharmacyDto2 {
  pharmacy_id: number;
  name: string;
  image_url: string;
  rating: number;
  distance_in_kilometers: number | null;
  opening_time: string;
  closing_time: string;
  latitude: number;
  longitude: number;
  review_count: number;
  is_closed: boolean;

  street: string;
  city: string;
  postal_code: string;
  country: string;
  apartment_number?: string;
}
export interface CreatePharmacyRequest {
  name: string;
  image?: File;
  openingTime: string;
  closingTime: string;
  is24Hours: boolean;
  street: string;
  city: string;
  postalCode: string;
  country: string;
  apartmentNumber: string;
  latitude: number;
  longitude: number;
}

export interface UpdatePharmacyRequest {
  name?: string;
  opening_time?: string;
  closing_time?: string;
  is_24_hours?: boolean;
  street?: string;
  city?: string;
  postal_code?: string;
  country?: string;
  apartment_number?: string;
  latitude?: number;
  longitude?: number;
}
