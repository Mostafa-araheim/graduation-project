import { VerificationComponent } from './Core/pages/verification/verification.component';
import { Routes } from '@angular/router';
import { HomeComponent } from './Core/pages/home/home.component';
import { MedicinesComponent } from './Core/pages/medicines/medicines.component';
import { PharmaciesComponent } from './Core/pages/pharmacies/pharmacies.component';
import { SellMedicineComponent } from './Core/pages/sell-medicine/sell-medicine.component';
import { PrescriptionComponent } from './Core/pages/prescription/prescription.component';
import { CartComponent } from './Core/pages/cart/cart.component';
import { PharmcyDetailsComponent } from './Core/pages/pharmcy-details/pharmcy-details.component';
import { ProductDetailsComponent } from './Core/pages/product-details/product-details.component';
import { SignInComponent } from './Core/pages/sign-in/sign-in.component';
import { SignUpComponent } from './Core/pages/sign-up/sign-up.component';
import { LoaderComponent } from './Shared/loader/loader.component';
import { MarketPlaceComponent } from './Core/pages/market-place/market-place.component';
import { ProfileComponent } from './Core/pages/profile/profile.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'findmedicines', component: MedicinesComponent },
  { path: 'pharmacies', component: PharmaciesComponent },
  { path: 'sell', component: SellMedicineComponent },
  { path: 'prescription', component: PrescriptionComponent },
  { path: 'cart', component: CartComponent },
  { path: 'pharmacyDetails/:id', component: PharmcyDetailsComponent },
  { path: 'productDetails/:id', component: ProductDetailsComponent },
  { path: 'signin', component: SignInComponent },
  { path: 'signup', component: SignUpComponent },
  { path: 'verify', component: VerificationComponent },
  { path: 'load', component: LoaderComponent },
  { path: 'marketPlace', component: MarketPlaceComponent },
  { path: 'profile', component: ProfileComponent },
];
