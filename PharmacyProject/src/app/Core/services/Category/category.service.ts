import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Environment } from '../../../Environment/environment';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  constructor(private _httpClient: HttpClient) {}

  GetCategories(): Observable<any> {
    return this._httpClient.get<any>(Environment.base_url + '/categories');
  }
}
