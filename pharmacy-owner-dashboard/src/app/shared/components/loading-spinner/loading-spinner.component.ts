import { Component, Input } from '@angular/core';
import { NgIf, NgClass } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [NgIf, NgClass],
  template: `
    <!-- Full Page Overlay -->
    <ng-container *ngIf="fullPage; else inlineSpinner">
      <div class="fixed inset-0 z-[9999] flex items-center justify-center bg-white/60 backdrop-blur-sm">
        <div class="flex flex-col items-center gap-3">
          <div
            class="rounded-full border-2 border-gray-200 animate-spin"
            [ngClass]="spinnerSizeClass"
            style="border-top-color: #3b82f6;"
          ></div>
          <p class="text-sm text-gray-500 font-medium">Loading...</p>
        </div>
      </div>
    </ng-container>

    <!-- Inline Spinner -->
    <ng-template #inlineSpinner>
      <div class="flex items-center justify-center">
        <div
          class="rounded-full border-2 border-gray-200 animate-spin"
          [ngClass]="spinnerSizeClass"
          style="border-top-color: #3b82f6;"
        ></div>
      </div>
    </ng-template>
  `,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class LoadingSpinnerComponent {
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() fullPage: boolean = false;

  get spinnerSizeClass(): string {
    switch (this.size) {
      case 'sm': return 'w-5 h-5';
      case 'md': return 'w-8 h-8';
      case 'lg': return 'w-12 h-12';
    }
  }
}
