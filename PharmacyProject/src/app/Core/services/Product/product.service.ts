import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Environment } from '../../../Environment/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private _httpClient: HttpClient) {}

  getProducts(
    search?: string | null,
    categoryName?: string | null,
    sort?: string | null,
    page: number = 0,
  ) {
    let params: any = {};
    params.size = 9;
    params.page = page;
    if (search) params.productName = search;
    if (categoryName) params.categoryName = categoryName;
    if (sort) params.sort = sort;

    return this._httpClient.get(Environment.base_url + '/pharmacy-products', {
      params,
    });
  }

  getProductById(id: number): Observable<any> {
    return this._httpClient.get(
      Environment.base_url + `/pharmacy-products/${id}`,
    );
  }

  getUniqueProducts(): Observable<any> {
    return this._httpClient.get(Environment.base_url + '/Products');
  }

  sellProductAsAUser(data: any): Observable<any> {
    return this._httpClient.post(Environment.base_url + '/p2p/listings', data);
  }

  getAllUserListedProduct(
    category?: string | null,
    city?: string | null,
    search?: string | null,
    page: number = 0,
    size: number = 6,
  ): Observable<any> {
    let params: any = {
      page,
      size,
    };

    if (category != null && category != '') params.categoryName = category;
    if (city != null && city != '') params.city = city;
    if (search != null) params.search = search;
    console.log(params);
    return this._httpClient.get(Environment.base_url + '/p2p/listings', {
      params,
    });
  }
}
