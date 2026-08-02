import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Environment } from '../../../Environment/environment';

@Injectable({
  providedIn: 'root',
})
export class PharmaciesService {
  constructor(private _httpClient: HttpClient) {}

  getAllpharmacies(
    lat?: number | null,
    long?: number | null,
    isOpen?: string | null,
    name?: string | null,
    page: number = 0,
    size: number = 6,
  ): Observable<any> {
    let params: any = {
      page,
      size,
    };

    if (lat != null) params.latitude = lat;
    if (long != null) params.longitude = long;

    if (name) params.name = name;

    if (isOpen === 'open') params.isOpen = true;
    else if (isOpen === 'all') params.isOpen = false;

    console.log(params);
    return this._httpClient.get<any>(Environment.base_url + `/pharmacies`, {
      params,
    });
  }

  getPharmaciesLocation(): Observable<any> {
    return this._httpClient.get<any>(
      Environment.base_url + '/pharmacies/locations',
    );
  }

  getPharmacyDetails(id: string): Observable<any> {
    return this._httpClient.get(Environment.base_url + `/pharmacies/${id}`);
  }

  getProductByPharmcyIdAndAcategoryId(
    pharmacyId: string,
    categoryId: string,
  ): Observable<any> {
    return this._httpClient.get(
      Environment.base_url + `/pharmacies/${pharmacyId}/${categoryId}`,
    );
  }
}
