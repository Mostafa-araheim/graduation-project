import { Component } from '@angular/core';
import { Subject } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.css',
})
export class SignInComponent {
  email = '';
  sending = false;
  sent = false;
  emailError = '';
  loading = true;

  features = [
    'Access 500+ verified pharmacies',
    'Compare prices instantly',
    'Fast doorstep delivery',
    'Passwordless, secure sign-in',
  ];

  private destroy$ = new Subject<void>();

  constructor(
    private _authService: AuthService,
    private _router: Router,
  ) {}

  loginForm: FormGroup = new FormGroup({
    email: new FormControl(null, [Validators.required, Validators.email]),
  });

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  submitForm() {
    if (this.loginForm.invalid || this.sending) return;

    this.sending = true;

    this._authService.signIn(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.sending = false;

        this._router.navigate(['/verify'], {
          queryParams: {
            type: 'signin',
            id: res.data.loginId,
          },
        });
      },

      error: (err) => {
        this.sending = false;
        console.log(err);
      },
    });
  }

  resetSent() {
    this.sent = false;
    this.email = '';
  }
}
