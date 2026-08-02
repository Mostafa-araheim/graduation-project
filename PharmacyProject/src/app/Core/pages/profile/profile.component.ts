import { Component, Signal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../../services/profile/profile.service';
import { LoaderComponent } from '../../../Shared/loader/loader.component';
import { Environment } from '../../../Environment/environment';

export interface Root {
  success: boolean;
  message: string;
  data: Data;
  error: any;
  timestamp: string;
}

export interface Data {
  content: Order[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  pageable: Pageable;
  size: number;
  sort: Sort2;
  totalElements: number;
  totalPages: number;
}

export interface Order {
  orderId: number;
  pharmacyId: number;
  pharmacyName: string;
  totalPrice: number;
  deliveryType: string;
  paymentMethod: string;
  status: string;
  createdAt: string;
  deliveryAddress: any;
  items: Item[];
}

export interface Item {
  productId: number;
  productName: string;
  productImage: string;
  quantity: number;
  priceAtPurchase: number;
  subtotal: number;
}

export interface Pageable {
  offset: number;
  pageNumber: number;
  pageSize: number;
  paged: boolean;
  sort: Sort;
  unpaged: boolean;
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Sort2 {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface ListedProducts {
  listingId: number;
  productId: number;
  productName: string;
  sellerId: number;
  sellerName: string;
  sellerPhoneNumber: string;
  categoryName: string;
  condition: any;
  quantity: number;
  price: number;
  expiryDate: string;
  description: any;
  imageUrl: string;
  status: string;
  createdAt: string;
  city: any;
}

export interface Address {
  addressId: number;
  street: string;
  city: string;
  postalcode: string;
  country: string;
  apartmentNumber: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, LoaderComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent {
  /**
   *
   */
  constructor(private _profileService: ProfileService) {}
  activeTab: string = 'orders';
  base = Environment.base;
  orders = signal<Order[]>([]);
  loading = signal<boolean>(false);
  profileImage: string | null = null;

  userName = 'John Doe';
  userEmail = 'john.doe@example.com';
  phoneNumber = '01012345678';

  showEditModal = false;

  editUserName = '';
  editPhoneNumber = '';

  statusColors: Record<string, string> = {
    CONFIRMED: 'bg-green-500 text-white',
    PLACED: 'bg-blue-500 text-white',
    PENDING_PAYMENT: 'bg-yellow-500 text-black',
  };

  addresses = signal<Address[]>([]);
  listItems = signal<ListedProducts[]>([]);

  navItems = [
    { label: 'My Orders', value: 'orders' },
    { label: 'Selling', value: 'selling' },
    { label: 'Addresses', value: 'addresses' },
  ];

  statsOrders = [
    { label: 'Total Orders', value: '24' },
    { label: 'Delivered', value: '21' },
    { label: 'In Progress', value: '2' },
    { label: 'Cancelled', value: '1' },
  ];

  statsSelling = [
    { label: 'Listed', value: '5' },
    { label: 'Pending', value: '2' },
    { label: 'Sold', value: '8' },
  ];

  sellingItems = [
    {
      id: '1',
      name: 'Vitamin D3 5000 IU',
      image:
        'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=200&auto=format',
      price: 15.0,
      status: 'Available',
      views: 45,
    },
    {
      id: '2',
      name: 'Omega-3 Fish Oil Capsules',
      image:
        'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=200&auto=format',
      price: 22.0,
      status: 'Pending',
      views: 12,
    },
    {
      id: '3',
      name: 'Probiotic Complex',
      image:
        'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=200&auto=format',
      price: 18.5,
      status: 'Sold',
      views: 89,
    },
  ];

  newAddress: Address = {
    addressId: 0,
    street: '',
    city: '',
    postalcode: '',
    country: '',
    apartmentNumber: '',
  };

  ngOnInit() {
    this.getMe();
    this.getOrders();
  }

  setActiveTab(tab: string) {
    if (tab == 'orders') {
      this.getOrders();
    }
    if (tab == 'addresses') {
      this.getAddresses();
    }
    if (tab == 'selling') {
      console.log(this.listItems());
      this.getListedProducts();
    }
    this.activeTab = tab;
  }

  addAddress() {
    this.loading.set(true);
    const address: Address = {
      ...this.newAddress,
    };

    this._profileService
      .AddAddress(
        this.newAddress.street,
        this.newAddress.city,
        this.newAddress.postalcode,
        this.newAddress.country,
        this.newAddress.apartmentNumber,
      )
      .subscribe({
        next: () => {
          this.addresses.update((current) => [...current, address]);

          this.newAddress = {
            addressId: 0,
            street: '',
            city: '',
            postalcode: '',
            country: '',
            apartmentNumber: '',
          };
          this.loading.set(false);
        },
        error: (err) => {
          console.log(err);
          this.loading.set(false);
        },
      });
  }

  deleteAddress(id: number) {
    this.loading.set(true);
    this._profileService.deleteAddrerss(id).subscribe({
      next: () => {
        this.addresses.set(this.addresses().filter((a) => a.addressId !== id));
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // 1. instant preview (local)
    const reader = new FileReader();

    reader.onload = () => {
      this.profileImage = reader.result as string;
    };

    reader.readAsDataURL(file);

    // 2. upload to server
    this._profileService.uploadPicture(file).subscribe({
      next: (res) => {
        console.log('Picture uploaded', res);

        // 🔥 IMPORTANT: replace preview with real backend URL
        if (res?.data?.imageUrl) {
          this.profileImage = `${res.data.imageUrl}?t=${Date.now()}`;
        }
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  openEditModal(): void {
    this.editUserName = this.userName;
    this.editPhoneNumber = this.phoneNumber;

    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
  }

  saveProfile(): void {
    this.loading.set(true);
    this._profileService
      .updateMe(this.editUserName, this.editPhoneNumber)
      .subscribe({
        next: () => {
          this.userName = this.editUserName;
          this.phoneNumber = this.editPhoneNumber;

          this.showEditModal = false;
          this.loading.set(false);
        },
        error: (err) => {
          console.log(err);
          this.loading.set(false);
        },
      });
  }

  getOrders() {
    this.loading.set(true);
    this._profileService.getUserOrders().subscribe({
      next: (res: Root) => {
        console.log(res.data.content);
        // res.data.content = this.orders;
        this.orders.set(res.data.content);
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }

  getAddresses() {
    this.loading.set(true);
    this._profileService.getUserAddresess().subscribe({
      next: (res) => {
        this.addresses.set(res.data);
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }
  getMe() {
    this.loading.set(true);
    this._profileService.getMe().subscribe({
      next: (res) => {
        console.log(res);
        const user = res.data;

        this.userName = user.name;
        this.userEmail = user.email;
        this.phoneNumber = user.phone;

        if (user.imageUrl) {
          this.profileImage = user.imageUrl;
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }

  getListedProducts() {
    this.loading.set(true);
    this._profileService.getListedItemsByUser().subscribe({
      next: (res) => {
        console.log(res);
        this.listItems.set(res.content);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
      },
    });
  }
}
