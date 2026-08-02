import { Component, AfterViewInit, OnInit } from '@angular/core';
import { CarouselModule, OwlOptions } from 'ngx-owl-carousel-o';
import { CategoryService } from '../../Core/services/Category/category.service';

interface Category {
  category_name: string;
  image_url: string;
  item_count: number;
}

@Component({
  selector: 'app-slider',
  standalone: true,
  imports: [CarouselModule],
  templateUrl: './slider.component.html',
  styleUrl: './slider.component.css',
})
export class SliderComponent implements OnInit {
  customOptions: OwlOptions = {
    loop: true,
    mouseDrag: true,
    touchDrag: true,
    pullDrag: false,
    dots: false,
    autoplay: true,
    autoplaySpeed: 700,
    autoplayTimeout: 2700,
    navSpeed: 700,
    navText: ['', ''],
    margin: 20,
    responsive: {
      0: {
        items: 5,
      },
    },
    nav: true,
  };

  categories!: Category[];

  constructor(private _categoryService: CategoryService) {}

  ngOnInit(): void {
    this._categoryService.GetCategories().subscribe({
      next: (res) => {
        console.log(res.data);

        this.categories = res.data;
      },
      error: (err) => {},
    });
  }
}
