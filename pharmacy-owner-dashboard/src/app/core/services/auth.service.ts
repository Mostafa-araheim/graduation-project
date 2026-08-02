import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import {
  LoginStartRequest,
  LoginStartResponse,
  LoginVerifyRequest,
  AuthVerification,
  SignupStartRequest,
  SignupStartResponse,
  SignupVerifyRequest,
} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private authUrl = environment.authUrl;

  private _isAuthenticated = signal<boolean>(this.hasToken());
  private _userId = signal<number | null>(this.getStoredUserId());
  private _userEmail = signal<string | null>(this.getStoredEmail());

  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly userId = this._userId.asReadonly();
  readonly userEmail = this._userEmail.asReadonly();

  // ── Step 1: Login Start ──────────────────────────────
  loginStart(email: string): Observable<ApiResponse<LoginStartResponse>> {
    const body: LoginStartRequest = { email, role: 'ROLE_OWNER' };
    return this.http.post<ApiResponse<LoginStartResponse>>(
      `${this.authUrl}/login/start`,
      body,
    );
  }

  // ── Step 2: Login Verify ─────────────────────────────
  loginVerify(loginId: string, code: string): Observable<AuthVerification> {
    const body: LoginVerifyRequest = { loginId, code };
    return this.http
      .post<
        ApiResponse<AuthVerification>
      >(`${this.authUrl}/login/verify`, body, { observe: 'response', withCredentials: true })
      .pipe(
        tap((response: HttpResponse<ApiResponse<AuthVerification>>) => {
          // JWT is in the Authorization response header
          const authHeader = response.headers.get('Authorization');
          if (authHeader) {
            const token = authHeader.replace('Bearer ', '');
            this.storeToken(token);
          }

          // Store user info from response body
          const body = response.body;
          if (body?.data) {
            localStorage.setItem('userId', String(body.data.userId));
            localStorage.setItem('userEmail', body.data.email);
            this._userId.set(body.data.userId);
            this._userEmail.set(body.data.email);
          }

          this._isAuthenticated.set(true);
        }),
        map((response) => response.body!.data!),
      );
  }

  // ── Signup Flow ──────────────────────────────────────
  signupStart(
    email: string,
    name: string,
  ): Observable<ApiResponse<SignupStartResponse>> {
    const body: SignupStartRequest = { email, name, role: 'ROLE_OWNER' };
    console.log(body);
    return this.http.post<ApiResponse<SignupStartResponse>>(
      `${this.authUrl}/signup/start`,
      body,
    );
  }

  signupVerify(signupId: string, code: string): Observable<AuthVerification> {
    const body: SignupVerifyRequest = { signupId, code };
    return this.http
      .post<
        ApiResponse<AuthVerification>
      >(`${this.authUrl}/signup/verify`, body, { observe: 'response', withCredentials: true })
      .pipe(
        tap((response: HttpResponse<ApiResponse<AuthVerification>>) => {
          const authHeader = response.headers.get('Authorization');
          if (authHeader) {
            const token = authHeader.replace('Bearer ', '');
            this.storeToken(token);
          }

          const body = response.body;
          if (body?.data) {
            localStorage.setItem('userId', String(body.data.userId));
            localStorage.setItem('userEmail', body.data.email);
            this._userId.set(body.data.userId);
            this._userEmail.set(body.data.email);
          }

          this._isAuthenticated.set(true);
        }),
        map((response) => response.body!.data!),
      );
  }

  // ── Token Refresh ────────────────────────────────────
  refreshToken(): Observable<void> {
    return this.http
      .post<
        ApiResponse<void>
      >(`${this.authUrl}/refresh`, null, { observe: 'response', withCredentials: true })
      .pipe(
        tap((response: HttpResponse<ApiResponse<void>>) => {
          const authHeader = response.headers.get('Authorization');
          if (authHeader) {
            // Backend may or may not include "Bearer " prefix on refresh
            const token = authHeader.replace('Bearer ', '');
            this.storeToken(token);
          }
        }),
        map(() => void 0),
      );
  }

  // ── Logout ───────────────────────────────────────────
  logout(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
    this._isAuthenticated.set(false);
    this._userId.set(null);
    this._userEmail.set(null);
    this.router.navigate(['/auth/login']);
  }

  // ── Token Utilities ──────────────────────────────────
  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  private storeToken(token: string): void {
    localStorage.setItem('access_token', token);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('access_token');
  }

  private getStoredUserId(): number | null {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id, 10) : null;
  }

  private getStoredEmail(): string | null {
    return localStorage.getItem('userEmail');
  }
}
