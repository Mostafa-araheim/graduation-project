import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  CreatePharmacyRequest,
  UpdatePharmacyRequest,
} from '../../../core/models/pharmacy.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-pharmacy-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  template: `
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="mb-8">
        <button
          (click)="goBack()"
          class="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-700 transition-colors mb-4"
        >
          <svg
            class="w-4 h-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M15 19l-7-7 7-7"
            />
          </svg>
          Back to Pharmacies
        </button>
        <h1 class="text-2xl font-bold text-gray-900 font-['Outfit']">
          {{ isEditMode() ? 'Edit Pharmacy' : 'Add New Pharmacy' }}
        </h1>
        <p class="text-sm text-gray-500 mt-1">
          {{
            isEditMode()
              ? 'Update your pharmacy details'
              : 'Fill in the details to register a new pharmacy'
          }}
        </p>
      </div>

      @if (formLoading()) {
        <app-loading-spinner message="Loading pharmacy data..." />
      } @else {
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-8">
          <!-- Basic Info -->
          <div
            class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm space-y-5"
          >
            <h2 class="text-lg font-semibold text-gray-900 font-['Outfit']">
              Basic Information
            </h2>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5"
                >Pharmacy Name *</label
              >
              <input
                formControlName="name"
                type="text"
                placeholder="e.g. Cairo Health Pharmacy"
                class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                            focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
              />
              @if (form.get('name')?.invalid && form.get('name')?.touched) {
                <p class="text-xs text-rose-500 mt-1">
                  Pharmacy name is required
                </p>
              }
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-3">
                Pharmacy Image
              </label>

              <div class="flex items-center gap-5">
                <div
                  class="w-28 h-28 rounded-2xl border-2 border-dashed border-gray-200 overflow-hidden bg-gray-50 flex items-center justify-center"
                >
                  @if (imagePreview()) {
                    <img
                      [src]="imagePreview()"
                      alt="Preview"
                      class="w-full h-full object-cover"
                    />
                  } @else {
                    <div class="text-center text-gray-400">
                      <div class="text-3xl">🏥</div>
                      <div class="text-xs">No Image</div>
                    </div>
                  }
                </div>

                <div>
                  <input
                    #imageInput
                    type="file"
                    accept="image/*"
                    class="hidden"
                    (change)="onImageSelected($event)"
                  />

                  <button
                    type="button"
                    (click)="imageInput.click()"
                    class="px-4 py-2.5 rounded-xl border border-gray-200 text-sm font-medium hover:bg-gray-50 transition-colors"
                  >
                    Choose Image
                  </button>

                  <p class="text-xs text-gray-500 mt-2">PNG, JPG, WEBP</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Address -->
          <div
            class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm space-y-5"
          >
            <h2 class="text-lg font-semibold text-gray-900 font-['Outfit']">
              Address
            </h2>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Street *</label
                >
                <input
                  formControlName="street"
                  type="text"
                  placeholder="123 Main Street"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (
                  form.get('street')?.invalid && form.get('street')?.touched
                ) {
                  <p class="text-xs text-rose-500 mt-1">Street is required</p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >City *</label
                >
                <input
                  formControlName="city"
                  type="text"
                  placeholder="Cairo"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (form.get('city')?.invalid && form.get('city')?.touched) {
                  <p class="text-xs text-rose-500 mt-1">City is required</p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Postal Code *</label
                >
                <input
                  formControlName="postal_code"
                  type="text"
                  placeholder="11511"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (
                  form.get('postal_code')?.invalid &&
                  form.get('postal_code')?.touched
                ) {
                  <p class="text-xs text-rose-500 mt-1">
                    Postal code is required
                  </p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Country *</label
                >
                <input
                  formControlName="country"
                  type="text"
                  placeholder="Egypt"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (
                  form.get('country')?.invalid && form.get('country')?.touched
                ) {
                  <p class="text-xs text-rose-500 mt-1">Country is required</p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Apartment Number</label
                >
                <input
                  formControlName="apartment_number"
                  type="text"
                  placeholder="Apt 4B"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
              </div>
            </div>
          </div>

          <!-- Location -->
          <div
            class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm space-y-5"
          >
            <h2 class="text-lg font-semibold text-gray-900 font-['Outfit']">
              Location Coordinates
            </h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Latitude *</label
                >
                <input
                  formControlName="latitude"
                  type="number"
                  step="any"
                  placeholder="30.0444"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (
                  form.get('latitude')?.invalid && form.get('latitude')?.touched
                ) {
                  <p class="text-xs text-rose-500 mt-1">Latitude is required</p>
                }
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5"
                  >Longitude *</label
                >
                <input
                  formControlName="longitude"
                  type="number"
                  step="any"
                  placeholder="31.2357"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                              focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                />
                @if (
                  form.get('longitude')?.invalid &&
                  form.get('longitude')?.touched
                ) {
                  <p class="text-xs text-rose-500 mt-1">
                    Longitude is required
                  </p>
                }
              </div>
            </div>
          </div>

          <!-- Hours -->
          <div
            class="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm space-y-5"
          >
            <h2 class="text-lg font-semibold text-gray-900 font-['Outfit']">
              Operating Hours
            </h2>

            <!-- 24 Hours Toggle -->
            <div class="flex items-center gap-3">
              <label class="relative inline-flex items-center cursor-pointer">
                <input
                  formControlName="is_24_hours"
                  type="checkbox"
                  class="sr-only peer"
                />
                <div
                  class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300/20
                            rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full
                            peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px]
                            after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5
                            after:transition-all peer-checked:bg-blue-600"
                ></div>
              </label>
              <span class="text-sm font-medium text-gray-700"
                >Open 24 Hours</span
              >
            </div>

            @if (!form.get('is_24_hours')?.value) {
              <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5"
                    >Opening Time *</label
                  >
                  <input
                    formControlName="opening_time"
                    type="time"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                                focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5"
                    >Closing Time *</label
                  >
                  <input
                    formControlName="closing_time"
                    type="time"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50/50
                                focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 outline-none transition-all text-sm"
                  />
                </div>
              </div>
            }
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-3 justify-end">
            <button
              type="button"
              (click)="goBack()"
              class="px-6 py-2.5 rounded-xl border border-gray-200 text-gray-700 text-sm font-medium
                           hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              [disabled]="form.invalid || submitting()"
              class="px-6 py-2.5 rounded-xl bg-gradient-to-r from-blue-600 to-blue-500 text-white text-sm
                           font-medium shadow-lg shadow-blue-500/25 hover:shadow-blue-500/40
                           transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              @if (submitting()) {
                <span class="inline-flex items-center gap-2">
                  <svg
                    class="w-4 h-4 animate-spin"
                    viewBox="0 0 24 24"
                    fill="none"
                  >
                    <circle
                      class="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      stroke-width="4"
                    />
                    <path
                      class="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                    />
                  </svg>
                  Saving...
                </span>
              } @else {
                {{ isEditMode() ? 'Update Pharmacy' : 'Create Pharmacy' }}
              }
            </button>
          </div>
        </form>
      }
    </div>
  `,
})
export class PharmacyFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private pharmacyService = inject(PharmacyService);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isEditMode = signal(false);
  formLoading = signal(false);
  submitting = signal(false);
  pharmacyId: number | null = null;
  imagePreview = signal<string | null>(null);
  selectedImage: File | null = null;

  form: FormGroup = this.fb.group({
    name: ['', Validators.required],
    street: ['', Validators.required],
    city: ['', Validators.required],
    postal_code: ['', Validators.required],
    country: ['', Validators.required],
    apartment_number: [''],
    opening_time: ['09:00'],
    closing_time: ['22:00'],
    is_24_hours: [false],
    latitude: [null as number | null, Validators.required],
    longitude: [null as number | null, Validators.required],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.pharmacyId = +id;
      this.loadPharmacy(this.pharmacyId);
    }
  }

  private loadPharmacy(id: number): void {
    this.formLoading.set(true);
    this.pharmacyService.getPharmacyById(id).subscribe({
      next: (pharmacy) => {
        const [street = '', city = '', country = ''] =
          pharmacy.address?.split(',').map((x) => x.trim()) ?? [];

        this.form.patchValue({
          name: pharmacy.name,
          street,
          city,
          country,

          // not available in DTO → keep empty or default
          postal_code: '',
          apartment_number: '',

          opening_time: pharmacy.opening_time,
          closing_time: pharmacy.closing_time,
          latitude: pharmacy.latitude,
          longitude: pharmacy.longitude,
        });

        this.imagePreview.set(pharmacy.image_url || null);

        this.formLoading.set(false);
      },
      error: () => {
        this.formLoading.set(false);
        this.router.navigate(['/pharmacies']);
      },
    });
  }

  onSubmit(): void {
    console.log('SUBMIT CLICKED');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    console.log('kaka');
    this.submitting.set(true);
    console.log();
    const formValue = this.form.getRawValue();

    if (this.isEditMode() && this.pharmacyId) {
      const request: UpdatePharmacyRequest = {
        name: formValue.name,
        opening_time: formValue.is_24_hours ? '00:00' : formValue.opening_time,
        closing_time: formValue.is_24_hours ? '23:59' : formValue.closing_time,
        is_24_hours: formValue.is_24_hours,
        street: formValue.street,
        city: formValue.city,
        postal_code: formValue.postal_code,
        country: formValue.country,
        apartment_number: formValue.apartment_number,
        latitude: formValue.latitude,
        longitude: formValue.longitude,
      };
      this.pharmacyService.updatePharmacy(this.pharmacyId, request).subscribe({
        next: () => {
          this.submitting.set(false);
          this.router.navigate(['/pharmacies']);
        },
        error: () => this.submitting.set(false),
      });
    } else {
      const formData = new FormData();

      formData.append('name', formValue.name);

      if (this.selectedImage) {
        formData.append('image', this.selectedImage);
      }

      formData.append(
        'openingTime',
        formValue.is_24_hours ? '00:00' : formValue.opening_time,
      );

      formData.append(
        'closingTime',
        formValue.is_24_hours ? '23:59' : formValue.closing_time,
      );

      formData.append('is24Hours', String(formValue.is_24_hours));

      formData.append('street', formValue.street);
      formData.append('city', formValue.city);
      formData.append('postalCode', formValue.postal_code);
      formData.append('country', formValue.country);

      formData.append('apartmentNumber', formValue.apartment_number || '');

      formData.append('latitude', String(formValue.latitude));
      formData.append('longitude', String(formValue.longitude));

      this.pharmacyService.createPharmacy(formData).subscribe({
        next: () => {
          this.submitting.set(false);
          this.router.navigate(['/pharmacies']);
        },
        error: () => {
          this.submitting.set(false);
        },
      });
    }
  }

  // selectedImage: File | null = null;

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      return;
    }

    this.selectedImage = input.files[0];

    const reader = new FileReader();

    reader.onload = () => {
      this.imagePreview.set(reader.result as string);
    };

    reader.readAsDataURL(this.selectedImage);
  }
  goBack(): void {
    this.router.navigate(['/pharmacies']);
  }
}
