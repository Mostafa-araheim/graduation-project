import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DecimalPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { Chart, registerables, ChartConfiguration } from 'chart.js';
import { forkJoin } from 'rxjs';

import { AnalyticsService } from '../../../core/services/analytics.service';
import { PharmacyService } from '../../../core/services/pharmacy.service';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { EgpCurrencyPipe } from '../../../shared/pipes/currency-egp.pipe';
import {
  OwnerDashboardSummaryResponse,
  SalesAnalyticsResponse,
} from '../../../core/models/analytics.model';
import { PharmacyDto } from '../../../core/models/pharmacy.model';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard-overview',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NgChartsModule,
    MetricCardComponent,
    EgpCurrencyPipe,
    DecimalPipe,
    DatePipe,
  ],
  template: `
    <!-- Page Header -->
    <div
      class="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
    >
      <div>
        <h1
          class="font-outfit text-2xl font-bold tracking-tight text-slate-800 sm:text-3xl"
        >
          Dashboard Overview
        </h1>
        <p class="mt-1 text-sm text-slate-500">
          Welcome back! Here's what's happening across your pharmacies.
        </p>
      </div>

      <!-- Pharmacy Selector -->
      @if (pharmacies().length > 1) {
        <div class="relative">
          <select
            class="appearance-none rounded-xl border border-slate-200 bg-white/80 px-4 py-2.5 pr-10 text-sm font-medium text-slate-700 shadow-sm backdrop-blur-sm transition-all hover:border-blue-300 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
            [ngModel]="selectedPharmacyId()"
            (ngModelChange)="onPharmacyChange($event)"
          >
            <option [ngValue]="0">All Pharmacies</option>
            @for (pharmacy of pharmacies(); track pharmacy.pharmacy_id) {
              <option [ngValue]="pharmacy.pharmacy_id">
                {{ pharmacy.name }}
              </option>
            }
          </select>
          <svg
            class="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="m19.5 8.25-7.5 7.5-7.5-7.5"
            />
          </svg>
        </div>
      }
    </div>

    <!-- Loading State -->
    @if (loading()) {
      <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
        @for (i of [1, 2, 3, 4, 5, 6]; track i) {
          <div
            class="animate-pulse rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
          >
            <div class="flex items-start justify-between">
              <div class="space-y-3 flex-1">
                <div class="h-3 w-20 rounded bg-gray-200"></div>
                <div class="h-7 w-16 rounded bg-gray-200"></div>
              </div>
              <div class="h-12 w-12 rounded-xl bg-gray-200"></div>
            </div>
          </div>
        }
      </div>
      <div class="mt-6 grid gap-6 lg:grid-cols-2">
        <div
          class="animate-pulse rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
        >
          <div class="h-4 w-36 rounded bg-gray-200 mb-4"></div>
          <div class="h-64 rounded-xl bg-gray-100"></div>
        </div>
        <div
          class="animate-pulse rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
        >
          <div class="h-4 w-36 rounded bg-gray-200 mb-4"></div>
          <div class="h-64 rounded-xl bg-gray-100"></div>
        </div>
      </div>
    }

    <!-- Main Content -->
    @if (!loading()) {
      <!-- KPI Metric Cards Row -->
      @if (summary()) {
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <app-metric-card
            label="Total Pharmacies"
            [value]="summary()!.total_pharmacies"
            icon="🏥"
            color="blue"
          />

          <app-metric-card
            label="Total Products"
            [value]="summary()!.total_products"
            icon="📦"
            color="purple"
          />

          <app-metric-card
            label="Out of Stock"
            [value]="summary()!.out_of_stock_count"
            icon="🚫"
            color="rose"
          />

          <app-metric-card
            label="Total Orders"
            [value]="summary()!.total_orders"
            icon="🛒"
            color="emerald"
          />

          <app-metric-card
            label="Total Revenue"
            [value]="summary()!.total_revenue | egp"
            icon="💰"
            color="amber"
          />

          <app-metric-card
            label="Limited Supply"
            [value]="summary()!.limited_supply_count"
            icon="⚠️"
            color="amber"
          />
        </div>
      }

      <!-- Charts Row -->
      @if (salesData()) {
        <div class="mt-6 grid gap-6 lg:grid-cols-5">
          <!-- Revenue Over Time - Line Chart -->
          <div
            class="lg:col-span-3 rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
          >
            <div class="mb-4 flex items-center justify-between">
              <div>
                <h3 class="font-outfit text-base font-semibold text-slate-800">
                  Revenue Over Time
                </h3>
                <p class="text-xs text-slate-400">Sales performance trends</p>
              </div>
              <div
                class="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-50"
              >
                <svg
                  class="h-4 w-4 text-blue-500"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="1.5"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M2.25 18 9 11.25l4.306 4.306a11.95 11.95 0 0 1 5.814-5.518l2.74-1.22m0 0-5.94-2.281m5.94 2.28-2.28 5.941"
                  />
                </svg>
              </div>
            </div>
            <div class="relative h-72">
              <canvas
                baseChart
                [datasets]="revenueChartData.datasets"
                [labels]="revenueChartData.labels"
                [options]="lineChartOptions"
                type="line"
              >
              </canvas>
            </div>
          </div>

          <!-- Order Status Distribution - Doughnut Chart -->
          <div
            class="lg:col-span-2 rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
          >
            <div class="mb-4 flex items-center justify-between">
              <div>
                <h3 class="font-outfit text-base font-semibold text-slate-800">
                  Order Status
                </h3>
                <p class="text-xs text-slate-400">Distribution by status</p>
              </div>
              <div
                class="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-50"
              >
                <svg
                  class="h-4 w-4 text-emerald-500"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="1.5"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M10.5 6a7.5 7.5 0 1 0 7.5 7.5h-7.5V6Z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M13.5 10.5H21A7.5 7.5 0 0 0 13.5 3v7.5Z"
                  />
                </svg>
              </div>
            </div>
            <div class="relative flex items-center justify-center h-64">
              <canvas
                baseChart
                [datasets]="doughnutChartData.datasets"
                [labels]="doughnutChartData.labels"
                [options]="doughnutChartOptions"
                type="doughnut"
              >
              </canvas>
            </div>
            <!-- Legend -->
            <div class="mt-4 flex flex-wrap gap-3 justify-center">
              @for (
                label of doughnutChartData.labels;
                track label;
                let i = $index
              ) {
                <div class="flex items-center gap-1.5 text-xs text-slate-600">
                  <span
                    class="inline-block h-2.5 w-2.5 rounded-full"
                    [style.background-color]="statusColors[i]"
                  ></span>
                  {{ label }}
                </div>
              }
            </div>
          </div>
        </div>

        <!-- Best Sellers Table -->
        @if (
          salesData()!.best_sellers && salesData()!.best_sellers.length > 0
        ) {
          <div
            class="mt-6 rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden"
          >
            <div class="border-b border-gray-100 px-6 py-4">
              <h3 class="font-outfit text-base font-semibold text-slate-800">
                Best Selling Products
              </h3>
              <p class="text-xs text-slate-400">
                Top performing products by revenue
              </p>
            </div>
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-gray-50 bg-slate-50/50">
                    <th
                      class="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-slate-400"
                    >
                      Rank
                    </th>
                    <th
                      class="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-slate-400"
                    >
                      Product Name
                    </th>
                    <th
                      class="px-6 py-3 text-right text-xs font-semibold uppercase tracking-wider text-slate-400"
                    >
                      Qty Sold
                    </th>
                    <th
                      class="px-6 py-3 text-right text-xs font-semibold uppercase tracking-wider text-slate-400"
                    >
                      Revenue
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-50">
                  @for (
                    seller of salesData()!.best_sellers;
                    track seller.productId;
                    let idx = $index
                  ) {
                    <tr
                      class="transition-colors hover:bg-blue-50/30"
                      [ngClass]="idx % 2 === 0 ? 'bg-white' : 'bg-slate-50'"
                    >
                      <td class="whitespace-nowrap px-6 py-3.5">
                        <div
                          class="flex h-7 w-7 items-center justify-center rounded-lg text-xs font-bold"
                          [ngClass]="{
                            'bg-amber-100 text-amber-700': idx === 0,
                            'bg-slate-100 text-slate-600': idx === 1,
                            'bg-orange-100 text-orange-700': idx === 2,
                            'bg-gray-50 text-gray-500': idx >= 3,
                          }"
                        >
                          {{ idx + 1 }}
                        </div>
                      </td>
                      <td
                        class="whitespace-nowrap px-6 py-3.5 text-sm font-medium text-slate-700"
                      >
                        {{ seller.productName }}
                      </td>
                      <td
                        class="whitespace-nowrap px-6 py-3.5 text-right text-sm text-slate-600"
                      >
                        {{ seller.quantitySold | number }}
                      </td>
                      <td
                        class="whitespace-nowrap px-6 py-3.5 text-right text-sm font-semibold text-emerald-600"
                      >
                        {{ seller.totalRevenue | egp }}
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        }
      }
    }
  `,
  styles: [
    `
      :host {
        display: block;
      }
      .font-outfit {
        font-family: 'Outfit', sans-serif;
      }
    `,
  ],
})
export class DashboardOverviewComponent implements OnInit {
  private analyticsService = inject(AnalyticsService);
  private pharmacyService = inject(PharmacyService);

  // ── State ──────────────────────────────────────────────
  readonly loading = signal(true);
  readonly summary = signal<OwnerDashboardSummaryResponse | null>(null);
  readonly salesData = signal<SalesAnalyticsResponse | null>(null);
  readonly pharmacies = signal<PharmacyDto[]>([]);
  readonly selectedPharmacyId = signal<number>(0);

  // ── Chart Data ─────────────────────────────────────────
  revenueChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [],
  };
  doughnutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [],
  };
  statusColors: string[] = [];

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
          label: (ctx) =>
            `Revenue: EGP ${(ctx.parsed.y ?? 0).toLocaleString('en-EG', { minimumFractionDigits: 2 })}`,
        },
      },
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: {
          font: { family: 'Inter', size: 11 },
          color: '#94a3b8',
          maxTicksLimit: 8,
        },
        border: { display: false },
      },
      y: {
        grid: { color: 'rgba(241, 245, 249, 0.8)' },
        ticks: {
          font: { family: 'Inter', size: 11 },
          color: '#94a3b8',
          callback: (value) => `${(+value / 1000).toFixed(0)}k`,
        },
        border: { display: false },
      },
    },
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
          label: (ctx) => `${ctx.label}: ${ctx.parsed} orders`,
        },
      },
    },
  };

  // ── Lifecycle ──────────────────────────────────────────
  ngOnInit(): void {
    this.loadPharmacies();
    this.loadDashboardData();
  }

  onPharmacyChange(pharmacyId: number): void {
    this.selectedPharmacyId.set(pharmacyId);
    this.loadDashboardData();
  }

  private loadPharmacies(): void {
    this.pharmacyService.getOwnerPharmacies({ size: 100 }).subscribe({
      next: (page) => this.pharmacies.set(page.content),
      error: () => {},
    });
  }

  private loadDashboardData(): void {
    this.loading.set(true);

    const summaryCall = this.analyticsService.getOwnerDashboardSummary();
    const pharmacyId = this.selectedPharmacyId();
    const salesCall =
      pharmacyId > 0
        ? this.analyticsService.getPharmacySalesAnalytics(pharmacyId, 'month')
        : this.analyticsService.getOwnerSalesAnalytics('month');

    forkJoin([summaryCall, salesCall]).subscribe({
      next: ([summaryData, salesDataRes]) => {
        this.summary.set(summaryData);
        this.salesData.set(salesDataRes);
        this.buildRevenueChart(salesDataRes);
        this.buildDoughnutChart(salesDataRes);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  private buildRevenueChart(data: SalesAnalyticsResponse): void {
    const labels = data.sales_over_time.map((s) => {
      const d = new Date(s.date);
      return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    });
    const values = data.sales_over_time.map((s) => s.revenue);

    this.revenueChartData = {
      labels,
      datasets: [
        {
          data: values,
          borderColor: '#3b82f6',
          backgroundColor: (ctx: any) => {
            const chart = ctx.chart;
            const { ctx: context, chartArea } = chart;
            if (!chartArea) return 'rgba(59, 130, 246, 0.1)';
            const gradient = context.createLinearGradient(
              0,
              chartArea.top,
              0,
              chartArea.bottom,
            );
            gradient.addColorStop(0, 'rgba(59, 130, 246, 0.15)');
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
        },
      ],
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
    this.doughnutChartData = {
      labels,
      datasets: [
        {
          data: values,
          backgroundColor: colors,
          borderColor: '#ffffff',
          borderWidth: 3,
          hoverBorderWidth: 0,
          hoverOffset: 8,
        },
      ],
    };
  }

  private formatStatus(status: string): string {
    return status
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/^\w/, (c) => c.toUpperCase());
  }
}
