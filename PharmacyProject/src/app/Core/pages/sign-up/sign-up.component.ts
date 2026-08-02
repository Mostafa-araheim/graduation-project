import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css',
})
export class SignUpComponent {
  /**
   *
   */
  constructor(
    private _authSAervice: AuthService,
    private router: Router,
  ) {}
  isLoading = false;
  registerForm: FormGroup = new FormGroup({
    name: new FormControl(null, [
      Validators.maxLength(20),
      Validators.min(3),
      Validators.required,
    ]),
    email: new FormControl(null, [Validators.email, Validators.required]),
  });

  submitRegister() {
    if (this.registerForm.invalid || this.isLoading) return;

    this.isLoading = true;

    this._authSAervice.signUp(this.registerForm.value).subscribe({
      next: (res) => {
        this.isLoading = false;

        this.router.navigate(['/verify'], {
          queryParams: {
            type: 'signup',
            id: res.data.signupId,
          },
        });
      },

      error: (err) => {
        this.isLoading = false;
        console.error(err);
      },
    });
  }
}
