import { Component, signal, computed, inject, DestroyRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginStartResponse } from '../../../core/models/auth.model';
import { ApiResponse } from '../../../core/models/api-response.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex">
      <!-- ════════════════════════════════════════════════════
           LEFT PANEL — Brand / Hero (hidden on mobile)
           ════════════════════════════════════════════════════ -->
      <div
        class="hidden lg:flex w-1/2 bg-gradient-to-br from-primary-600 via-primary-700 to-primary-900
               flex-col justify-center items-center p-12 text-white relative overflow-hidden"
      >
        <!-- Decorative floating circles -->
        <div class="absolute -top-20 -left-20 w-72 h-72 rounded-full bg-white/10 blur-sm"></div>
        <div class="absolute top-1/4 right-10 w-48 h-48 rounded-full bg-white/5"></div>
        <div class="absolute bottom-20 left-16 w-36 h-36 rounded-full bg-white/[0.07]"></div>
        <div class="absolute -bottom-10 -right-10 w-64 h-64 rounded-full bg-white/[0.04]"></div>
        <div class="absolute top-10 left-1/3 w-20 h-20 rounded-full bg-white/10"></div>
        <div class="absolute bottom-1/3 right-1/4 w-14 h-14 rounded-full bg-accent-400/20"></div>

        <!-- Content -->
        <div class="relative z-10 max-w-md text-center lg:text-left">
          <!-- Brand -->
          <div class="flex items-center gap-3 mb-6 justify-center lg:justify-start">
            <div class="w-12 h-12 rounded-xl bg-white/20 backdrop-blur-sm flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
              </svg>
            </div>
            <h1 class="text-4xl font-heading font-bold">PharmaOwner</h1>
          </div>

          <!-- Tagline -->
          <p class="text-xl text-primary-100 mb-10 leading-relaxed">
            Your Complete Pharmacy Management Solution
          </p>

          <!-- Feature list -->
          <div class="space-y-4">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-white/15 flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-accent-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <span class="text-primary-100 text-sm font-medium">Manage inventory across all your pharmacies</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-white/15 flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-accent-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <span class="text-primary-100 text-sm font-medium">Track orders & fulfillment in real-time</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-white/15 flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-accent-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <span class="text-primary-100 text-sm font-medium">Analyze sales & performance analytics</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ════════════════════════════════════════════════════
           RIGHT PANEL — Login Form
           ════════════════════════════════════════════════════ -->
      <div class="w-full lg:w-1/2 flex items-center justify-center bg-slate-50 p-6 sm:p-8">
        <!-- Mobile brand header -->
        <div class="w-full max-w-md">
          <div class="lg:hidden flex items-center gap-2 mb-8 justify-center">
            <div class="w-10 h-10 rounded-xl bg-primary-600 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
              </svg>
            </div>
            <span class="text-xl font-heading font-bold text-gray-900">PharmaOwner</span>
          </div>

          <!-- Form Card -->
          <div class="bg-white rounded-2xl shadow-card p-8">

            <!-- ──────── STEP 1: Email Input ──────── -->
            <div [class.hidden]="step() !== 1">
              <div class="mb-8">
                <h2 class="font-heading text-3xl font-bold text-gray-900 mb-2">Welcome Back</h2>
                <p class="text-gray-500 text-sm">Enter your email to receive a verification code</p>
              </div>

              <form [formGroup]="emailForm" (ngSubmit)="onEmailSubmit()">
                <div class="mb-6">
                  <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
                    Email Address
                  </label>
                  <div class="relative">
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <svg class="w-5 h-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M21.75 6.75v10.5a2.25 2.25 0 01-2.25 2.25h-15a2.25 2.25 0 01-2.25-2.25V6.75m19.5 0A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25m19.5 0v.243a2.25 2.25 0 01-1.07 1.916l-7.5 4.615a2.25 2.25 0 01-2.36 0L3.32 8.91a2.25 2.25 0 01-1.07-1.916V6.75" />
                      </svg>
                    </div>
                    <input
                      id="email"
                      type="email"
                      formControlName="email"
                      class="input-field !pl-12"
                      placeholder="you@example.com"
                      autocomplete="email"
                    />
                  </div>
                  @if (emailForm.controls.email.touched && emailForm.controls.email.errors) {
                    <p class="mt-2 text-xs text-red-500 flex items-center gap-1">
                      <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
                      </svg>
                      @if (emailForm.controls.email.errors['required']) {
                        Email is required
                      } @else if (emailForm.controls.email.errors['email']) {
                        Please enter a valid email address
                      }
                    </p>
                  }
                </div>

                <!-- Error message -->
                @if (errorMessage() && step() === 1) {
                  <div class="mb-4 p-3 rounded-xl bg-red-50 border border-red-100 text-red-600 text-sm flex items-center gap-2">
                    <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round"
                            d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126z" />
                    </svg>
                    {{ errorMessage() }}
                  </div>
                }

                <button
                  type="submit"
                  class="btn-primary w-full"
                  [disabled]="emailForm.invalid || loading()"
                >
                  @if (loading()) {
                    <svg class="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                      </path>
                    </svg>
                    Sending Code...
                  } @else {
                    Continue
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
                    </svg>
                  }
                </button>
              </form>
            </div>

            <!-- ──────── STEP 2: OTP Verification ──────── -->
            <div [class.hidden]="step() !== 2">
              <div class="mb-8">
                <h2 class="font-heading text-3xl font-bold text-gray-900 mb-2">Verify Your Identity</h2>
                <p class="text-gray-500 text-sm">
                  Enter the 8-digit code sent to
                  <span class="font-semibold text-gray-700">{{ email() }}</span>
                </p>
                <button
                  type="button"
                  (click)="goBack()"
                  class="mt-1 text-primary-600 text-sm font-medium hover:text-primary-700 hover:underline transition-colors"
                >
                  ← Change email
                </button>
              </div>

              <form [formGroup]="otpForm" (ngSubmit)="onOtpSubmit()">
                <div class="mb-6">
                  <label for="otp" class="block text-sm font-medium text-gray-700 mb-2">
                    Verification Code
                  </label>
                  <input
                    id="otp"
                    type="text"
                    formControlName="code"
                    class="input-field text-center text-2xl tracking-[0.5em] font-mono"
                    placeholder="• • • • • • • •"
                    maxlength="8"
                    autocomplete="one-time-code"
                  />
                  @if (otpForm.controls.code.touched && otpForm.controls.code.errors) {
                    <p class="mt-2 text-xs text-red-500 flex items-center gap-1">
                      <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
                      </svg>
                      @if (otpForm.controls.code.errors['required']) {
                        Verification code is required
                      } @else if (otpForm.controls.code.errors['minlength'] || otpForm.controls.code.errors['maxlength']) {
                        Code must be exactly 8 digits
                      }
                    </p>
                  }
                </div>

                <!-- Countdown Timer -->
                <div class="mb-6 text-center">
                  @if (countdown() > 0) {
                    <div class="flex items-center justify-center gap-2 text-gray-400 text-sm">
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      Code expires in
                      <span class="font-mono font-semibold text-gray-600">{{ formattedCountdown() }}</span>
                    </div>
                  } @else {
                    <p class="text-amber-600 text-sm font-medium">Code has expired</p>
                  }
                </div>

                <!-- Error message -->
                @if (errorMessage() && step() === 2) {
                  <div class="mb-4 p-3 rounded-xl bg-red-50 border border-red-100 text-red-600 text-sm flex items-center gap-2">
                    <svg class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round"
                            d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126z" />
                    </svg>
                    {{ errorMessage() }}
                  </div>
                }

                <button
                  type="submit"
                  class="btn-primary w-full mb-4"
                  [disabled]="otpForm.invalid || loading() || countdown() === 0"
                >
                  @if (loading()) {
                    <svg class="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                      </path>
                    </svg>
                    Verifying...
                  } @else {
                    <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round"
                            d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
                    </svg>
                    Verify Code
                  }
                </button>

                <!-- Resend Code -->
                <div class="text-center">
                  <button
                    type="button"
                    (click)="resendCode()"
                    [disabled]="countdown() > 0 || loading()"
                    class="text-sm font-medium transition-colors"
                    [class]="countdown() > 0
                      ? 'text-gray-300 cursor-not-allowed'
                      : 'text-primary-600 hover:text-primary-700 hover:underline'"
                  >
                    @if (countdown() > 0) {
                      Resend code available after timer expires
                    } @else {
                      Resend verification code
                    }
                  </button>
                </div>
              </form>
            </div>

          </div>

          <!-- Footer text -->
          <p class="mt-6 text-center text-sm text-gray-500">
            Don't have an account?
            <a routerLink="/auth/signup" class="font-semibold text-primary-600 hover:text-primary-700 hover:underline">
              Create one
            </a>
          </p>
          <p class="mt-6 text-center text-xs text-gray-400">
            By continuing, you agree to PharmaOwner's Terms of Service and Privacy Policy.
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class LoginComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  // ── State Signals ──
  step = signal<1 | 2>(1);
  loading = signal(false);
  errorMessage = signal('');
  loginId = signal('');
  countdown = signal(180);
  email = signal('');

  // ── Computed ──
  formattedCountdown = computed(() => {
    const total = this.countdown();
    const minutes = Math.floor(total / 60);
    const seconds = total % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  });

  // ── Forms ──
  emailForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email])
  });

  otpForm = new FormGroup({
    code: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(8)
    ])
  });

  // ── Timer ──
  private countdownInterval: ReturnType<typeof setInterval> | null = null;

  ngOnInit(): void {
    // Clean up interval on destroy
    this.destroyRef.onDestroy(() => {
      this.clearCountdown();
    });
  }

  // ── Step 1: Email Submit ──
  onEmailSubmit(): void {
    if (this.emailForm.invalid) return;

    const emailValue = this.emailForm.controls.email.value!;
    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.loginStart(emailValue).subscribe({
      next: (response) => {
        if (response.data) {
          this.loginId.set(response.data.loginId);
          this.email.set(emailValue);
          this.step.set(2);
          this.startCountdown();
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(
          err.error?.message || err.error?.error?.details?.[0] || 'Failed to send verification code. Please try again.'
        );
        this.loading.set(false);
      }
    });
  }

  // ── Step 2: OTP Submit ──
  onOtpSubmit(): void {
    if (this.otpForm.invalid) return;

    const code = this.otpForm.controls.code.value!;
    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.loginVerify(this.loginId(), code).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage.set(
          err.error?.message || err.error?.error?.details?.[0] || 'Invalid verification code. Please try again.'
        );
        this.loading.set(false);
      }
    });
  }

  // ── Go Back to Step 1 ──
  goBack(): void {
    this.step.set(1);
    this.clearCountdown();
    this.countdown.set(180);
    this.errorMessage.set('');
    this.otpForm.reset();
  }

  // ── Resend Code ──
  resendCode(): void {
    if (this.countdown() > 0 || this.loading()) return;

    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.loginStart(this.email()).subscribe({
      next: (response) => {
        if (response.data) {
          this.loginId.set(response.data.loginId);
          this.countdown.set(180);
          this.startCountdown();
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(
          err.error?.message || err.error?.error?.details?.[0] || 'Failed to resend code. Please try again.'
        );
        this.loading.set(false);
      }
    });
  }

  // ── Countdown Timer ──
  private startCountdown(): void {
    this.clearCountdown();
    this.countdownInterval = setInterval(() => {
      const current = this.countdown();
      if (current <= 1) {
        this.countdown.set(0);
        this.clearCountdown();
      } else {
        this.countdown.set(current - 1);
      }
    }, 1000);
  }

  private clearCountdown(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }
}
