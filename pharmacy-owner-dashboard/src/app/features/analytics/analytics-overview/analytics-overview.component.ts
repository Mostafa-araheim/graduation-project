import { Component, OnInit, inject, signal, effect } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { Chart, registerables, ChartConfiguration } from 'chart.js';

import { AnalyticsService } from '../../../core/services/analytics.service';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { EgpCurrencyPipe } from '../../../shared/pipes/currency-egp.pipe';
import { SalesAnalyticsResponse } from '../../../core/models/analytics.model';
import { PharmacyDto } from '../../../core/models/pharmacy.model';
import { AnalyticsPeriod } from '../../../core/models/enums.model';

Chart.register(...registerables);

@Component({
  selector: 'app-analytics-overview',
  standalone: true,
  imports: [CommonModule, FormsModule, NgChartsModule, MetricCardComponent, EgpCurrencyPipe, DecimalPipe],
  template: `
    <!-- Page Header -->
    <div class="mb-8">
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 class="font-outfit text-2xl font-bold tracking-tight text-slate-800 sm:text-3xl">
            Sales Analytics
          </h1>
          <p class="mt-1 text-sm text-slate-500">Track revenue, orders, and product performance over time.</p>
        </div>

        <!-- Pharmacy Selector -->
        <div class="relative">
          <select
            class="appearance-none rounded-xl border border-slate-200 bg-white/80 px-4 py-2.5 pr-10 text-sm font-medium text-slate-700 shadow-sm backdrop-blur-sm transition-all hover:border-blue-300 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            [ngModel]="selectedPharmacyId()"
            (ngModelChange)="onPharmacyChange($event)">
            <option [ngValue]="0">All Pharmacies</option>
            @for (pharmacy of pharmacies(); track pharmacy.pharmacy_id) {
              <option [ngValue]="pharmacy.pharmacy_id">{{ pharmacy.name }}</option>
            }
          </select>
          <svg class="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
          </svg>
        </div>
      </div>

      <!-- Period Selector Pills -->
      <div class="mt-5 inline-flex items-center rounded-xl border border-slate-200 bg-slate-50/80 p-1 shadow-sm">
        @for (period of periods; track period.value) {
          <button
            type="button"
            class="rounded-lg px-5 py-2 text-sm font-medium transition-all duration-200"
            [class]="selectedPeriod() === period.value
              ? 'bg-white text-blue-600 shadow-sm ring-1 ring-black/5'
              : 'text-slate-500 hover:text-slate-700'"
            (click)="onPeriodChange(period.value)">
            {{ period.label }}
          </button>
        }
      </div>
    </div>

    <!-- Loading State -->
    @if (loading()) {
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        @for (i of [1,2,3]; track i) {
          <div class="animate-pulse rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
            <div class="flex items-start justify-between">
              <div class="space-y-3 flex-1">
                <div class="h-3 w-24 rounded bg-gray-200"></div>
                <div class="h-7 w-20 rounded bg-gray-200"></div>
              </div>
              <div class="h-12 w-12 rounded-xl bg-gray-200"></div>
            </div>
          </div>
        }
      </div>
      <div class="mt-6 animate-pulse rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
        <div class="h-4 w-36 rounded bg-gray-200 mb-4"></div>
        <div class="h-80 rounded-xl bg-gray-100"></div>
      </div>
    }

    <!-- Main Content -->
    @if (!loading() && analytics()) {
      <!-- KPI Row -->
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <app-metric-card
          label="Total Revenue"
          [value]="analytics()!.total_revenue | egp"
          icon="💰"
          color="emerald"
        />
        <app-metric-card
          label="Total Orders"
          [value]="analytics()!.total_orders"
          icon="🛒"
          color="blue"
        />
        <app-metric-card
          label="Avg. Order Value"
          [value]="analytics()!.average_order_value | egp"
          icon="📊"
          color="purple"
        />
      </div>

      <!-- Revenue Line Chart (Full Width) -->
      <div class="mt-6 rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
        <div class="mb-4 flex items-center justify-between">
          <div>
            <h3 class="font-outfit text-base font-semibold text-slate-800">Revenue Over Time</h3>
            <p class="text-xs text-slate-400">
              {{ periodLabel }} performance · {{ analytics()!.sales_over_time.length }} data points
            </p>
          </div>
          <div class="flex items-center gap-2 text-sm">
            <span class="inline-block h-2.5 w-2.5 rounded-full bg-blue-500"></span>
            <span class="text-slate-500">Revenue</span>
          </div>
        </div>
        <div class="relative h-80">
          <canvas baseChart
            [datasets]="revenueChartData.datasets"
            [labels]="revenueChartData.labels"
            [options]="lineChartOptions"
            type="line">
          </canvas>
        </div>
      </div>

      <!-- Bottom Row: Best Sellers + Doughnut -->
      <div class="mt-6 grid gap-6 lg:grid-cols-5">
        <!-- Best Sellers Table -->
        <div class="lg:col-span-3 rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
          <div class="border-b border-gray-100 px-6 py-4">
            <h3 class="font-outfit text-base font-semibold text-slate-800">Best Selling Products</h3>
            <p class="text-xs text-slate-400">Top performers by quantity and revenue</p>
          </div>

          @if (analytics()!.best_sellers && analytics()!.best_sellers.length > 0) {
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-gray-50 bg-slate-50/50">
                    <th class="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">Rank</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-slate-400">Product Name</th>
                    <th class="px-6 py-3 text-right text-xs font-semibold uppercase tracking-wider text-slate-400">Qty Sold</th>
                    <th class="px-6 py-3 text-right text-xs font-semibold uppercase tracking-wider text-slate-400">Revenue</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-50">
                  @for (seller of analytics()!.best_sellers; track seller.productId; let idx = $index) {
                    <tr class="transition-colors hover:bg-blue-50/30"
                        [ngClass]="idx % 2 === 0 ? 'bg-white' : 'bg-slate-50'">
                      <td class="whitespace-nowrap px-6 py-3.5">
                        <div class="flex h-7 w-7 items-center justify-center rounded-lg text-xs font-bold"
                             [ngClass]="{
                               'bg-amber-100 text-amber-700': idx === 0,
                               'bg-slate-100 text-slate-600': idx === 1,
                               'bg-orange-100 text-orange-700': idx === 2,
                               'bg-gray-50 text-gray-500': idx >= 3
                             }">
                          {{ idx + 1 }}
                        </div>
                      </td>
                      <td class="whitespace-nowrap px-6 py-3.5">
                        <span class="text-sm font-medium text-slate-700">{{ seller.productName }}</span>
                      </td>
                      <td class="whitespace-nowrap px-6 py-3.5 text-right text-sm text-slate-600">
                        {{ seller.quantitySold | number }}
                      </td>
                      <td class="whitespace-nowrap px-6 py-3.5 text-right text-sm font-semibold text-emerald-600">
                        {{ seller.totalRevenue | egp }}
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          } @else {
            <div class="flex flex-col items-center justify-center py-12 text-center">
              <svg class="h-12 w-12 text-slate-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                <path stroke-linecap="round" stroke-linejoin="round" d="m20.25 7.5-.625 10.632a2.25 2.25 0 0 1-2.247 2.118H6.622a2.25 2.25 0 0 1-2.247-2.118L3.75 7.5m8.25 3v6.75m0 0-3-3m3 3 3-3M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125Z" />
              </svg>
              <p class="mt-3 text-sm text-slate-400">No sales data for this period</p>
            </div>
          }
        </div>

        <!-- Order Status Distribution - Doughnut Chart -->
        <div class="lg:col-span-2 rounded-2xl border border-gray-100 bg-white p-6 shadow-sm">
          <div class="mb-4">
            <h3 class="font-outfit text-base font-semibold text-slate-800">Order Status</h3>
            <p class="text-xs text-slate-400">Distribution by status</p>
          </div>
          <div class="relative flex items-center justify-center h-56">
            <canvas baseChart
              [datasets]="doughnutChartData.datasets"
              [labels]="doughnutChartData.labels"
              [options]="doughnutChartOptions"
              type="doughnut">
            </canvas>
          </div>
          <!-- Legend -->
          <div class="mt-4 space-y-2">
            @for (label of doughnutChartData.labels; track label; let i = $index) {
              <div class="flex items-center justify-between text-sm">
                <div class="flex items-center gap-2">
                  <span class="inline-block h-3 w-3 rounded-full" [style.background-color]="statusColors[i]"></span>
                  <span class="text-slate-600">{{ label }}</span>
                </div>
                <span class="font-medium text-slate-800">{{ doughnutValues[i] | number }}</span>
              </div>
            }
          </div>
        </div>
      </div>
    }

    <!-- Empty state -->
    @if (!loading() && !analytics()) {
      <div class="mt-12 flex flex-col items-center justify-center rounded-2xl border border-dashed border-gray-200 bg-white py-16 text-center">
        <svg class="h-16 w-16 text-slate-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3 13.125C3 12.504 3.504 12 4.125 12h2.25c.621 0 1.125.504 1.125 1.125v6.75C7.5 20.496 6.996 21 6.375 21h-2.25A1.125 1.125 0 0 1 3 19.875v-6.75ZM9.75 8.625c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125v11.25c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V8.625ZM16.5 4.125c0-.621.504-1.125 1.125-1.125h2.25C20.496 3 21 3.504 21 4.125v15.75c0 .621-.504 1.125-1.125 1.125h-2.25a1.125 1.125 0 0 1-1.125-1.125V4.125Z" />
        </svg>
        <h3 class="mt-4 font-outfit text-lg font-semibold text-slate-700">No Analytics Data</h3>
        <p class="mt-2 max-w-sm text-sm text-slate-400">
          Analytics data will appear here once orders start coming in. Try selecting a different period or pharmacy.
        </p>
      </div>
    }
  `,
  styles: [`
    :host { display: block; }
    .font-outfit { font-family: 'Outfit', sans-serif; }
  `]
})
export class AnalyticsOverviewComponent implements OnInit {
  private analyticsService = inject(AnalyticsService);
  private pharmacyService = inject(PharmacyService);

  // ── State ──────────────────────────────────────────────
  readonly loading = signal(true);
  readonly analytics = signal<SalesAnalyticsResponse | null>(null);
  readonly pharmacies = signal<PharmacyDto[]>([]);
  readonly selectedPeriod = signal<AnalyticsPeriod>('month');
  readonly selectedPharmacyId = signal<number>(0);

  readonly periods: { label: string; value: AnalyticsPeriod }[] = [
    { label: 'Week', value: 'week' },
    { label: 'Month', value: 'month' },
    { label: 'Year', value: 'year' },
  ];

  // ── Chart Data ─────────────────────────────────────────
  revenueChartData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  doughnutChartData: ChartConfiguration<'doughnut'>['data'] = { labels: [], datasets: [] };
  statusColors: string[] = [];
  doughnutValues: number[] = [];

  lineChartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        titleFont: { family: 'Inter', size: 12 },
        bodyFont: { family: 'Inter', size: 12 },
        padding: 12,
        cornerRadius: 10,
        displayColors: false,
        callbacks: {
          label: (ctx) => `Revenue: EGP ${(ctx.parsed.y ?? 0).toLocaleString('en-EG', { minimumFractionDigits: 2 })}`
        }
      }
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: { font: { family: 'Inter', size: 11 }, color: '#94a3b8', maxTicksLimit: 12 },
        border: { display: false }
      },
      y: {
        grid: { color: 'rgba(241, 245, 249, 0.8)' },
        ticks: {
          font: { family: 'Inter', size: 11 },
          color: '#94a3b8',
          callback: (value) => `${(+value / 1000).toFixed(0)}k`
        },
        border: { display: false }
      }
    }
  };

  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '68%',
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        titleFont: { family: 'Inter' },
        bodyFont: { family: 'Inter' },
        padding: 12,
        cornerRadius: 10,
        callbacks: {
          label: (ctx) => `${ctx.label}: ${ctx.parsed} orders`
        }
      }
    }
  };

  // ── Computed ────────────────────────────────────────────
  get periodLabel(): string {
    const map: Record<AnalyticsPeriod, string> = {
      week: 'Weekly',
      month: 'Monthly',
      year: 'Yearly'
    };
    return map[this.selectedPeriod()];
  }

  // ── Lifecycle ──────────────────────────────────────────
  ngOnInit(): void {
    this.loadPharmacies();
    this.fetchAnalytics();
  }

  onPeriodChange(period: AnalyticsPeriod): void {
    this.selectedPeriod.set(period);
    this.fetchAnalytics();
  }

  onPharmacyChange(pharmacyId: number): void {
    this.selectedPharmacyId.set(pharmacyId);
    this.fetchAnalytics();
  }

  private loadPharmacies(): void {
    this.pharmacyService.getOwnerPharmacies({ size: 100 }).subscribe({
      next: (page) => this.pharmacies.set(page.content),
      error: () => {}
    });
  }

  private fetchAnalytics(): void {
    this.loading.set(true);
    const period = this.selectedPeriod();
    const pharmacyId = this.selectedPharmacyId();

    const call$ = pharmacyId > 0
      ? this.analyticsService.getPharmacySalesAnalytics(pharmacyId, period)
      : this.analyticsService.getOwnerSalesAnalytics(period);

    call$.subscribe({
      next: (data) => {
        this.analytics.set(data);
        this.buildRevenueChart(data);
        this.buildDoughnutChart(data);
        this.loading.set(false);
      },
      error: () => {
        this.analytics.set(null);
        this.loading.set(false);
      }
    });
  }

  private buildRevenueChart(data: SalesAnalyticsResponse): void {
    const labels = data.sales_over_time.map(s => {
      const d = new Date(s.date);
      return this.selectedPeriod() === 'year'
        ? d.toLocaleDateString('en-US', { month: 'short' })
        : d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    });
    const values = data.sales_over_time.map(s => s.revenue);

    this.revenueChartData = {
      labels,
      datasets: [{
        data: values,
        borderColor: '#3b82f6',
        backgroundColor: (ctx: any) => {
          const chart = ctx.chart;
          const { ctx: context, chartArea } = chart;
          if (!chartArea) return 'rgba(59, 130, 246, 0.1)';
          const gradient = context.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
          gradient.addColorStop(0, 'rgba(59, 130, 246, 0.18)');
          gradient.addColorStop(1, 'rgba(59, 130, 246, 0.01)');
          return gradient;
        },
        borderWidth: 2.5,
        tension: 0.4,
        fill: true,
        pointBackgroundColor: '#3b82f6',
        pointBorderColor: '#ffffff',
        pointBorderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 6,
        pointHoverBorderWidth: 3,
      }]
    };
  }

  private buildDoughnutChart(data: SalesAnalyticsResponse): void {
    const colorMap: Record<string, string> = {
      CONFIRMED: '#10b981',
      PLACED: '#3b82f6',
      PENDING_PAYMENT: '#f59e0b',
      FAILED: '#ef4444',
      CANCELED: '#f43f5e',
    };

    const entries = Object.entries(data.status_distribution);
    const labels = entries.map(([key]) => this.formatStatus(key));
    const values = entries.map(([, val]) => val);
    const colors = entries.map(([key]) => colorMap[key] || '#94a3b8');

    this.statusColors = colors;
    this.doughnutValues = values;
    this.doughnutChartData = {
      labels,
      datasets: [{
        data: values,
        backgroundColor: colors,
        borderColor: '#ffffff',
        borderWidth: 3,
        hoverBorderWidth: 0,
        hoverOffset: 8,
      }]
    };
  }

  private formatStatus(status: string): string {
    return status
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/^\w/, c => c.toUpperCase());
  }
}
