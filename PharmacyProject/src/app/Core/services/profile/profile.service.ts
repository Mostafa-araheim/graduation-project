import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Environment } from '../../../Environment/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  constructor(private _httpClient: HttpClient) {}

  getUserOrders(): Observable<any> {
    let params: any = {};

    params.sort = 'createdAt,desc';
    return this._httpClient.get(Environment.base_url + '/orders', { params });
  }

  getUserAddresess(): Observable<any> {
    return this._httpClient.get(Environment.base_url + '/users/me/addresses');
  }

  AddAddress(
    street: string,
    city: string,
    postalcode: string,
    country: string,
    apartmentNumber: string,
  ): Observable<any> {
    return this._httpClient.post(Environment.base_url + '/users/me/addresses', {
      street,
      city,
      postalcode,
      country,
      apartmentNumber,
    });
  }

  UpdateAdress() {}

  deleteAddrerss(addressId: number): Observable<any> {
    console.log(addressId);
    return this._httpClient.delete(
      Environment.base_url + `/users/me/addresses/${addressId}`,
    );
  }

  uploadPicture(file: File): Observable<any> {
    const formData = new FormData();

    formData.append('file', file);

    return this._httpClient.post(
      Environment.base_url + '/users/me/picture',
      formData,
    );
  }

  getMe(): Observable<any> {
    return this._httpClient.get(Environment.base_url + '/users/me');
  }

  updateMe(name: string | null, phone: string | null) {
    return this._httpClient.put(Environment.base_url + '/users/me', {
      name,
      phone,
    });
  }

  getListedItemsByUser(): Observable<any> {
    return this._httpClient.get(Environment.base_url + '/p2p/listings/me');
  }
}
