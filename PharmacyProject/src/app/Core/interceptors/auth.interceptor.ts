import {
  BehaviorSubject,
  catchError,
  filter,
  Observable,
  of,
  switchMap,
  take,
  throwError,
} from 'rxjs';

import { Environment } from '../../Environment/environment';

import {
  HttpInterceptor,
  HttpClient,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse,
} from '@angular/common/http';

import { Injectable, Inject, PLATFORM_ID } from '@angular/core';

import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    // Skip refresh endpoint
    if (req.url.includes('/auth/refresh')) {
      return next.handle(req);
    }

    let token: string | null = null;

    // ✅ localStorage only in browser
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('PharmacyAccessToken');
    }

    let authReq = req.clone({
      withCredentials: true,
    });

    if (token) {
      authReq = authReq.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status !== 401) {
          return throwError(() => error);
        }

        return this.handle401Error(authReq, next);
      }),
    );
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.refreshToken().pipe(
        switchMap((newToken: string) => {
          this.isRefreshing = false;

          // ✅ browser check
          if (isPlatformBrowser(this.platformId)) {
            localStorage.setItem('PharmacyAccessToken', newToken);
          }

          this.refreshTokenSubject.next(newToken);

          return next.handle(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`,
              },
              withCredentials: true,
            }),
          );
        }),

        catchError((err) => {
          this.handleAuthFailure();
          return throwError(() => err);
        }),
      );
    }

    return this.refreshTokenSubject.pipe(
      filter((token) => token != null),
      take(1),
      switchMap((token) =>
        next.handle(
          req.clone({
            setHeaders: {
              Authorization: `Bearer ${token!}`,
            },
            withCredentials: true,
          }),
        ),
      ),
    );
  }

  private refreshToken(): Observable<string> {
    return this.http
      .post<any>(
        Environment.base_url + '/auth/refresh',
        {},
        { withCredentials: true },
      )
      .pipe(
        switchMap((res) => {
          return of(res.accessToken);
        }),
      );
  }

  private handleAuthFailure() {
    this.isRefreshing = false;

    // ✅ browser check
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('PharmacyAccessToken');
    }

    this.refreshTokenSubject.next(null);
    this.refreshTokenSubject.complete();

    this.refreshTokenSubject = new BehaviorSubject<string | null>(null);

    this.router.navigate(['/login']);
  }
}
