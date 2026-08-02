import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { CartService } from '../../services/cart/cart.service';
import { switchMap } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verification',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.css',
})
export class VerificationComponent implements OnInit {
  otpArray: string[] = new Array(8).fill('');

  type: 'signup' | 'signin' = 'signup';
  id!: string;

  isLoading = false;
  errorMessage = '';

  constructor(
    private auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private cart: CartService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.type = params['type'];
      this.id = params['id'];
    });
  }

  get otpCode(): string {
    return this.otpArray.join('');
  }

  onOtpInput(event: any, index: number) {
    const value = event.target.value;

    if (!/^\d?$/.test(value)) {
      this.otpArray[index] = '';
      return;
    }

    this.otpArray[index] = value;

    const next = document.getElementById(`otp-${index + 1}`);
    if (value && next) {
      (next as HTMLInputElement).focus();
    }

    if (this.otpArray.every((d) => d !== '')) {
      this.verifyCode();
    }
  }

  verifyCode() {
    if (this.isLoading) return;
    if (this.otpArray.some((d) => !d)) return;

    this.isLoading = true;
    this.errorMessage = '';

    const code = this.otpCode;

    const request$ =
      this.type === 'signup'
        ? this.auth.verifySignUp(this.id, code)
        : this.auth.verifySignIn(this.id, code);

    request$
      .pipe(
        switchMap((res) => {
          const token = res.headers.get('authorization');

          if (token) {
            const clean = token.replace('Bearer ', '');
            localStorage.setItem('PharmacyAccessToken', clean);
          }

          return this.cart.assignAnonymousCartToUser();
        }),
      )
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Invalid verification code. Please try again.';
          console.error(err);
        },
      });
  }

  onOtpKeydown(event: KeyboardEvent, index: number) {
    if (event.key === 'Backspace' && !this.otpArray[index] && index > 0) {
      (
        document.getElementById(`otp-${index - 1}`) as HTMLInputElement
      )?.focus();
    }

    if (event.key === 'ArrowLeft' && index > 0) {
      (
        document.getElementById(`otp-${index - 1}`) as HTMLInputElement
      )?.focus();
    }

    if (event.key === 'ArrowRight' && index < 7) {
      (
        document.getElementById(`otp-${index + 1}`) as HTMLInputElement
      )?.focus();
    }
  }

  onOtpPaste(event: ClipboardEvent) {
    event.preventDefault();

    const data = event.clipboardData?.getData('text') || '';

    if (!/^\d{8}$/.test(data)) return;

    this.otpArray = data.split('');
    (document.getElementById('otp-7') as HTMLInputElement)?.focus();

    this.verifyCode();
  }
}
