import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'egp',
  standalone: true
})
export class EgpCurrencyPipe implements PipeTransform {
  transform(value: number | null | undefined, showSymbol: boolean = true): string {
    if (value === null || value === undefined) return '—';
    const formatted = value.toLocaleString('en-EG', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
    return showSymbol ? `EGP ${formatted}` : formatted;
  }
}
