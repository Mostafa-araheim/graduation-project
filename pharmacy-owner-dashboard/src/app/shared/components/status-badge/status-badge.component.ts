import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [NgClass],
  template: `
    <span [ngClass]="badgeClass">
      {{ formattedStatus }}
    </span>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: string;

  get badgeClass(): string {
    const s = this.status?.toUpperCase() ?? '';

    switch (s) {
      case 'CONFIRMED':
      case 'AVAILABLE':
      case 'ACTIVE':
      case 'DELIVERED':
      case 'COMPLETED':
        return 'badge-success';

      case 'PENDING_PAYMENT':
      case 'PLACED':
      case 'LIMITEDSUPPLY':
      case 'LIMITED_SUPPLY':
      case 'PENDING':
      case 'PROCESSING':
        return 'badge-warning';

      case 'FAILED':
      case 'CANCELED':
      case 'CANCELLED':
      case 'OUTOFSTOCK':
      case 'OUT_OF_STOCK':
      case 'REJECTED':
        return 'badge-danger';

      default:
        return 'badge-neutral';
    }
  }

  get formattedStatus(): string {
    if (!this.status) return '';
    return this.status
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }
}
