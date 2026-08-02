import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  Menu,
  X,
  Search,
  ShoppingCart,
  User,
  Pill,
  LucideAngularModule,
} from 'lucide-angular';
import { AuthService } from '../../services/auth/auth.service';
import { CartService } from '../../services/cart/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, LucideAngularModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  isMenuOpen = false;
  isLanding = false;

  // Lucide icons
  menuIcon = Menu;
  xIcon = X;
  searchIcon = Search;
  shoppingCartIcon = ShoppingCart;
  userIcon = User;
  pillIcon = Pill;

  navLinks = [
    { href: '/findmedicines', label: 'Find Medicines' },
    { href: '/pharmacies', label: 'Pharmacies' },
    { href: '/marketPlace', label: 'MarketPlace' },
    { href: '/sell', label: 'Sell Medicine' },
    { href: '/prescription', label: 'Upload Prescription' },
  ];

  cartItems = 3;

  constructor(
    private router: Router,
    public authService: AuthService,
    public cartService: CartService,
  ) {
    this.isLanding = this.router.url === '/';
    this.router.events.subscribe(() => {
      this.isLanding = this.router.url === '/';
    });
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  isActive(href: string): boolean {
    return this.router.url === href;
  }

  // Handle escape key
  handleKeydown(event: KeyboardEvent) {
    if (event.key === 'Escape' && this.isMenuOpen) {
      this.closeMenu();
    }
  }
}
