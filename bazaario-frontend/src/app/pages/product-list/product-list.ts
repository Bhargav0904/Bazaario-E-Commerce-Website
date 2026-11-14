import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ProductService } from '../../core/product';
import { CategoryService } from '../../core/category';
import { Product } from '../../models/product.model';
import { Category } from '../../models/category.model';
import { API_BASE } from '../../core/api.config';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {
  readonly apiBase = API_BASE;
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  keyword = '';
  selectedCategoryId = signal<number | null>(null);
  loading = signal(true);

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    // Re-runs whenever the URL query changes (header search, nav links, category circles)
    this.route.queryParamMap.subscribe((params) => {
      this.keyword = params.get('keyword') ?? '';
      const category = params.get('categoryId');
      this.selectedCategoryId.set(category ? Number(category) : null);
      this.loadProducts();
    });
    this.categoryService.getAll().subscribe({ next: (cats) => this.categories.set(cats) });
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService
      .getAll(this.keyword || undefined, this.selectedCategoryId() ?? undefined)
      .subscribe({
        next: (data) => {
          this.products.set(data);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  onSearch(): void {
    this.selectedCategoryId.set(null);
    this.loadProducts();
  }

  filterByCategory(categoryId: number | null): void {
    this.keyword = '';
    this.selectedCategoryId.set(categoryId);
    this.loadProducts();
  }
}
