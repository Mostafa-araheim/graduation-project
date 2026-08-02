import { Data } from './../pharmcy-details/pharmcy-details.component';
import { ProductService } from './../../services/Product/product.service';
import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  Validators,
} from '@angular/forms';
import { log } from 'console';
import {
  LucideAngularModule,
  Info,
  ChevronDown,
  Check,
  Form,
} from 'lucide-angular';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

export interface Products {
  success: boolean;
  message: string;
  data: Product[];
  error: any;
  timestamp: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  requiresPrescription: boolean;
  dosageForm: string;
  strength: string;
  manufacturer: string;
  category: string;
}

@Component({
  selector: 'app-sell-medicine',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LucideAngularModule,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './sell-medicine.component.html',
  styleUrl: './sell-medicine.component.css',
})
export class SellMedicineComponent implements OnInit {
  constructor(
    private _messageService: MessageService,
    private _productService: ProductService,
  ) {}

  ngOnInit() {
    this._productService.getUniqueProducts().subscribe({
      next: (res) => {
        console.log(res);
        this.products = res.data;
        this.displayedProducts = this.products.slice(0, 5);
        this.displayedCity = this.cities.slice(0, 5);
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  // sellProductForm: FormGroup = new FormGroup({
  //   productId: new FormControl(null, [
  //     Validators.required,
  //     Validators.pattern('^\d+$'),
  //   ]),
  //   condition: new FormControl(null, [Validators.required]),
  //   quantity: new FormControl(null, [Validators.required]),
  //   description: new FormControl(null, [Validators.required]),
  //   price: new FormControl(null, [Validators.required]),
  //   expiryDate: new FormControl(null, [Validators.required]),
  // });

  // submitForm() {}

  info = Info;
  chevronDown = ChevronDown;
  check = Check;
  // ---------- state ----------
  images: string[] = [];

  products: Product[] = [];
  cities = [
    { location: 'Cairo', state: 'Cairo' },
    { location: 'Giza', state: 'Giza' },
    { location: 'Alexandria', state: 'Alexandria' },
    { location: 'Dakahlia', state: 'Dakahlia' },
    { location: 'Red Sea', state: 'Red Sea' },
    { location: 'Beheira', state: 'Beheira' },
    { location: 'Fayoum', state: 'Fayoum' },
    { location: 'Gharbia', state: 'Gharbia' },
    { location: 'Ismailia', state: 'Ismailia' },
    { location: 'Menofia', state: 'Menofia' },
    { location: 'Minya', state: 'Minya' },
    { location: 'Qalyubia', state: 'Qalyubia' },
    { location: 'New Valley', state: 'New Valley' },
    { location: 'Suez', state: 'Suez' },
    { location: 'Aswan', state: 'Aswan' },
    { location: 'Assiut', state: 'Assiut' },
    { location: 'Beni Suef', state: 'Beni Suef' },
    { location: 'Port Said', state: 'Port Said' },
    { location: 'Damietta', state: 'Damietta' },
    { location: 'Sharkia', state: 'Sharkia' },
    { location: 'South Sinai', state: 'South Sinai' },
    { location: 'Kafr El Sheikh', state: 'Kafr El Sheikh' },
    { location: 'Matrouh', state: 'Matrouh' },
    { location: 'Luxor', state: 'Luxor' },
    { location: 'Qena', state: 'Qena' },
    { location: 'North Sinai', state: 'North Sinai' },
    { location: 'Sohag', state: 'Sohag' },
  ];
  conditions = [
    { name: 'SEALED' },
    { name: 'OPENED_UNUSED' },
    { name: 'PARTIALLY_USED' },
    { name: 'Acceptable' },
  ];
  isSubmitting = false;
  displayedProducts: Product[] = [];
  displayedCity: { location: string; state: string }[] = [];
  // form model
  form = {
    productId: '',
    city: '',
    condition: '',
    description: '',
    price: '',
    quantity: '',
    expiry: '',
  };

  selectedImageFile: File | null = null;
  imagePreview: string | null = null;
  // errors
  errors: any = {};
  imageError = '';

  // ---------- image handling ----------
  // onFileChange(event: Event) {
  //   const input = event.target as HTMLInputElement;

  //   if (!input.files || input.files.length === 0) return;

  //   const file = input.files[0];

  //   this.selectedImageFile = file;
  //   this.imagePreview = URL.createObjectURL(file);

  //   console.log('Selected image file:', file);
  // }
  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;
    const files = Array.from(input.files);
    const file = input.files[0];
    this.selectedImageFile = file;
    const urls = files.map((f) => URL.createObjectURL(f));
    this.images = [...this.images, ...urls].slice(0, 5);
    this.imageError = '';
    input.value = '';
  }

  removeImage(index: number) {
    URL.revokeObjectURL(this.images[index]);
    this.images.splice(index, 1);
    this.images = [...this.images];
  }

  // ---------- validation ----------
  clearErrors() {
    this.errors = {};
    this.imageError = '';
  }

  validate(): boolean {
    this.clearErrors();
    let hasError = false;

    // if (!this.form.productId) {
    //   this.errors.name = 'Medicine name is required';
    //   hasError = true;
    // }

    if (!this.form.city) {
      this.errors.city = 'City is required';
      hasError = true;
    }

    if (!this.form.condition) {
      this.errors.condition = 'Condition is required';
      hasError = true;
    }

    if (!this.form.price) {
      this.errors.price = 'Price is required';
      hasError = true;
    }

    if (!this.form.expiry) {
      this.errors.expiry = 'Expiry date is required';
      hasError = true;
    }

    if (this.images.length === 0) {
      this.imageError = 'At least one image is required';
      hasError = true;
    }
    console.log(hasError);

    return !hasError;
  }

  onFilter(event: any) {
    const query = event.filter?.toLowerCase() || '';

    if (!query) {
      // show only first 5 when search is empty
      this.displayedProducts = this.products.slice(0, 5);
      return;
    }

    // search in all products
    this.displayedProducts = this.products.filter((p) =>
      p.name.toLowerCase().includes(query),
    );
  }
  onCitiesFilter(event: any) {
    const query = event.filter?.toLowerCase() || '';

    if (!query) {
      // show only first 5 when search is empty
      this.displayedCity = this.cities.slice(0, 5);
      return;
    }

    // search in all products
    this.displayedCity = this.cities.filter((p) =>
      p.location.toLowerCase().includes(query),
    );
  }

  // ---------- submit ----------
  submit() {
    if (this.isSubmitting) return;
    if (!this.validate()) return;

    this.isSubmitting = true;

    console.log(this.form);
    const formData = new FormData();

    formData.append('productId', this.form.productId);
    formData.append('price', this.form.price);
    formData.append('quantity', this.form.quantity);
    formData.append('description', this.form.description);
    formData.append('condition', this.form.condition);
    formData.append('expiryDate', this.form.expiry);
    formData.append('city', this.form.city);

    if (this.selectedImageFile) {
      formData.append('image', this.selectedImageFile);
    }
    console.log(formData);
    this._productService.sellProductAsAUser(formData).subscribe({
      next: () => {
        this.isSubmitting = false;

        // Show PrimeNG toast
        this._messageService.add({
          severity: 'success', // success | info | warn | error
          summary: 'Success',
          detail: 'Medicine listed successfully! (demo)',
          life: 3000, // duration in ms
        });
      },
      error: () => {
        this.isSubmitting = false;

        // Show PrimeNG toast
        this._messageService.add({
          severity: 'error', // success | info | warn | error
          summary: 'error',
          detail: 'Medicine failed To list! (demo)',
          life: 3000, // duration in ms
        });
      },
    });
  }
}
