import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  LucideAngularModule,
  Pill,
  Mail,
  Phone,
  MapPin,
  Facebook,
  Twitter,
  Instagram,
  Linkedin,
} from 'lucide-angular';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink, LucideAngularModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent {
  // Icons
  pillIcon = Pill;
  mailIcon = Mail;
  phoneIcon = Phone;
  mapPinIcon = MapPin;
  facebookIcon = Facebook;
  twitterIcon = Twitter;
  instagramIcon = Instagram;
  linkedinIcon = Linkedin;

  currentYear: string = new Date().getFullYear().toString();
  // Quick Links
  quickLinks = [
    { label: 'Find Medicines', path: '/search' },
    { label: 'Browse Pharmacies', path: '/pharmacies' },
    { label: 'Sell Medicine', path: '/sell' },
    { label: 'Upload Prescription', path: '/prescription' },
    { label: 'My Account', path: '/profile' },
  ];

  // For Pharmacies
  pharmacyLinks = [
    { label: 'Register Your Pharmacy', path: '/pharmacy/register' },
    { label: 'Pharmacy Dashboard', path: '/pharmacy/dashboard' },
    { label: 'Subscription Plans', path: '/pricing' },
    { label: 'Support', path: '/support' },
  ];

  // Contact Info
  contactInfo = [
    { icon: this.mailIcon, text: 'support@mediconnect.com' },
    { icon: this.phoneIcon, text: '+1 (555) 123-4567' },
    {
      icon: this.mapPinIcon,
      text: '123 Healthcare Avenue, Medical District, NY 10001',
    },
  ];

  // Social Media Links
  socialLinks = [
    { icon: this.facebookIcon, url: '#' },
    { icon: this.twitterIcon, url: '#' },
    { icon: this.instagramIcon, url: '#' },
    { icon: this.linkedinIcon, url: '#' },
  ];
}
