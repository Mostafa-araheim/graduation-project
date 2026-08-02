import { Component, Input, Output, EventEmitter } from '@angular/core';
@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent {
  @Input() currentPage: number = 1;
  @Input() totalPages: number = 1;

  @Output() pageChange = new EventEmitter<number>();

  pages: number[] = [];

  ngOnChanges() {
    this.generatePages();
  }

  generatePages() {
    const maxPages = 10;
    let start = Math.max(1, this.currentPage - Math.floor(maxPages / 2));
    let end = start + maxPages - 1;

    if (end > this.totalPages) {
      end = this.totalPages;
      start = Math.max(1, end - maxPages + 1);
    }

    this.pages = [];

    for (let i = start; i <= end; i++) {
      this.pages.push(i);
    }
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;

    this.pageChange.emit(page - 1);
  }

  next() {
    this.goToPage(this.currentPage + 1);
  }

  previous() {
    this.goToPage(this.currentPage - 1);
  }

  first() {
    this.goToPage(1);
  }

  last() {
    this.goToPage(this.totalPages);
  }
}
