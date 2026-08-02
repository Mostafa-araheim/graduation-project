import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Observable } from 'rxjs';
import { Environment } from '../../../Environment/environment';
import { isPlatformBrowser } from '@angular/common';

export interface SignUp {
  name: string;
  email: string;
}

export interface SignIn {
  email: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private _httpClient: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  getToken(): string | null {
    if (!this.isBrowser()) return null;
    return localStorage.getItem('PharmacyAccessToken');
  }

  isLogin(): boolean {
    return !!this.getToken();
  }

  signUp(data: SignUp): Observable<any> {
    return this._httpClient.post(Environment.base_url + '/auth/signup/start', {
      name: data.name,
      email: data.email,
      role: 'ROLE_CUSTOMER',
    });
  }

  signIn(data: SignIn): Observable<any> {
    return this._httpClient.post(Environment.base_url + '/auth/login/start', {
      email: data.email,
      role: 'ROLE_CUSTOMER',
    });
  }

  verifySignUp(signUpId: string, code: string): Observable<any> {
    let body = {
      signupId: signUpId,
      code: code,
    };

    return this._httpClient.post(
      Environment.base_url + '/auth/signup/verify',
      body,
      {
        observe: 'response',
        withCredentials: true,
      },
    );
  }

  verifySignIn(signInId: string, code: string): Observable<any> {
    let body = {
      loginId: signInId,
      code: code,
    };

    return this._httpClient.post(
      Environment.base_url + '/auth/login/verify',
      body,
      {
        observe: 'response',
        withCredentials: true,
      },
    );
  }

  refreshToken(): Observable<any> {
    return this._httpClient.post(Environment.base_url + '/auth/refresh', {});
  }

  logout(): void {
    if (!this.isBrowser()) return;

    localStorage.removeItem('PharmacyAccessToken');
    localStorage.removeItem('userLocation');
  }
}
