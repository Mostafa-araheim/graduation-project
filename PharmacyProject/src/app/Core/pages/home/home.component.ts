import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  LucideAngularModule,
  Search,
  MapPin,
  Shield,
  Truck,
  Clock,
  Upload,
  Pill,
  Store,
  Star,
  ArrowRight,
  ChevronRight,
  Users,
} from 'lucide-angular';
import { CategoryService } from '../../services/Category/category.service';
import { SliderComponent } from '../../../Shared/slider/slider.component';

interface Feature {
  icon: any;
  title: string;
  description: string;
}
interface Stat {
  value: string;
  label: string;
  icon?: any;
}
interface Category {
  category_name: string;
  image_url: string;
  item_count: number;
}

interface HowItWorksItem {
  step: string;
  icon: any;
  title: string;
  description: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, SliderComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  search = Search;
  map_pin = MapPin;
  arrowRight = ArrowRight;
  chevronRight = ChevronRight;
  store = Store;
  truck = Truck;
  users = Users;
  quickActions = [
    { label: 'Upload Prescription', icon: Upload },
    { label: 'Sell Medicine', icon: Pill },
  ];

  features: Feature[] = [
    {
      icon: MapPin,
      title: 'Nearby Pharmacies',
      description:
        'Find pharmacies closest to you with real-time distance ordering',
    },
    {
      icon: Shield,
      title: 'Verified Sellers',
      description: 'All pharmacies are verified and licensed for your safety',
    },
    {
      icon: Truck,
      title: 'Fast Delivery',
      description: 'Get your medicines delivered quickly to your doorstep',
    },
    {
      icon: Clock,
      title: '24/7 Available',
      description: 'Browse and order medicines anytime, anywhere',
    },
  ];

  stats: Stat[] = [
    { value: '500+', label: 'Pharmacies' },
    { value: '50K+', label: 'Products' },
    { value: '100K+', label: 'Happy Users' },
    { value: '4.8', label: 'App Rating', icon: Star },
  ];

  howItWorks: HowItWorksItem[] = [
    {
      step: '01',
      icon: Search,
      title: 'Search',
      description: 'Find the medicine you need or browse nearby pharmacies',
    },
    {
      step: '02',
      icon: Store,
      title: 'Compare',
      description: 'Compare prices and availability across multiple pharmacies',
    },
    {
      step: '03',
      icon: Truck,
      title: 'Order',
      description: 'Place your order and get it delivered to your doorstep',
    },
  ];
}
