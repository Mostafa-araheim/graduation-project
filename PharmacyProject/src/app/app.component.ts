import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { FooterComponent } from './Core/layouts/footer/footer.component';
import { NavbarComponent } from './Core/layouts/navbar/navbar.component';
import { HomeComponent } from './Core/pages/home/home.component';
import { PharmaciesComponent } from './Core/pages/pharmacies/pharmacies.component';
import { MedicinesComponent } from './Core/pages/medicines/medicines.component';
import { ProductCardComponent } from './Shared/product-card/product-card.component';
import { SearchFilterComponent } from './Shared/search-filter/search-filter.component';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FooterComponent, NavbarComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'PharmacyProject';
  isSigninPage = false;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.isSigninPage =
          this.router.url === '/signin' ||
          this.router.url === '/signup' ||
          this.router.url === '/verify';
      });
  }
}
