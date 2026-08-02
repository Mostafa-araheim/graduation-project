import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-metric-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="relative overflow-hidden rounded-2xl bg-white border border-gray-100 p-6 shadow-sm
                hover:shadow-md transition-all duration-300 group">
      <!-- Background decoration -->
      <div class="absolute top-0 right-0 w-24 h-24 rounded-full opacity-5 -translate-y-6 translate-x-6"
           [ngClass]="resolvedColorClass"></div>

      <div class="flex items-start justify-between">
        <div class="space-y-2">
          <p class="text-sm font-medium text-gray-500">{{ resolvedTitle }}</p>
          <p class="text-2xl font-bold text-gray-900 tracking-tight font-['Outfit']">{{ value }}</p>
          @if (subtitle) {
            <p class="text-xs text-gray-400">{{ subtitle }}</p>
          }
        </div>
        <div class="flex-shrink-0 w-12 h-12 rounded-xl flex items-center justify-center shadow-sm"
             [ngClass]="resolvedColorClass">
          @if (isEmoji) {
            <span class="text-xl">{{ icon }}</span>
          } @else {
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" [attr.d]="icon" />
            </svg>
          }
        </div>
      </div>

      <!-- Trend Badge -->
      @if (trendValue) {
        <div class="mt-3 inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-semibold"
             [ngClass]="{
               'bg-emerald-50 text-emerald-600': trend === 'up',
               'bg-red-50 text-red-500': trend === 'down',
               'bg-gray-100 text-gray-500': trend === 'neutral'
             }">
          @if (trend === 'up') {
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 15l7-7 7 7" />
            </svg>
          }
          @if (trend === 'down') {
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 9l-7 7-7-7" />
            </svg>
          }
          <span>{{ trendValue }}</span>
        </div>
      }
    </div>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class MetricCardComponent implements OnChanges {
  /** Primary title input */
  @Input() title: string = '';
  /** Alias for title - used by dashboard-overview and other components */
  @Input() label: string = '';
  @Input({ required: true }) value!: string | number;
  @Input() subtitle: string = '';
  @Input() icon: string = '';
  @Input() trend: 'up' | 'down' | 'neutral' = 'neutral';
  @Input() trendValue: string = '';
  /** Direct class input for icon background, e.g. 'bg-primary-500' */
  @Input() colorClass: string = '';
  /** Shorthand color name, maps to Tailwind classes */
  @Input() color: 'blue' | 'emerald' | 'amber' | 'rose' | 'purple' | '' = '';

  resolvedTitle = '';
  resolvedColorClass = '';
  isEmoji = false;

  private static readonly COLOR_MAP: Record<string, string> = {
    blue: 'bg-blue-100 text-blue-600',
    emerald: 'bg-emerald-100 text-emerald-600',
    amber: 'bg-amber-100 text-amber-600',
    rose: 'bg-rose-100 text-rose-600',
    purple: 'bg-purple-100 text-purple-600',
  };

  ngOnChanges(): void {
    this.resolvedTitle = this.label || this.title;
    this.resolvedColorClass = this.color
      ? (MetricCardComponent.COLOR_MAP[this.color] || 'bg-blue-100 text-blue-600')
      : (this.colorClass || 'bg-blue-100 text-blue-600');
    // Detect emoji icons (non-SVG path strings)
    this.isEmoji = !!this.icon && !this.icon.startsWith('M') && !this.icon.startsWith('m');
  }
}
