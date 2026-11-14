import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ProductService } from '../../core/product';
import { CartService } from '../../core/cart';
import { Auth } from '../../core/auth';
import { Product } from '../../models/product.model';
import { API_BASE } from '../../core/api.config';

@Component({
  selector: 'app-product-detail',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
  readonly apiBase = API_BASE;
  product = signal<Product | null>(null);
  quantity = 1;
  message = signal<string | null>(null);
  error = signal<string | null>(null);
  loading = signal(true);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    public auth: Auth,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getById(id).subscribe({
      next: (p) => {
        this.product.set(p);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Product not found');
        this.loading.set(false);
      },
    });
  }

  addToCart(): void {
    const p = this.product();
    if (!p) return;

    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: `/products/${p.productId}` } });
      return;
    }

    this.message.set(null);
    this.error.set(null);

    this.cartService.addItem(p.productId, this.quantity).subscribe({
      next: () => this.message.set('Added to cart'),
      error: (err) => this.error.set(err.error?.message ?? 'Could not add to cart'),
    });
  }

  changeQty(delta: number): void {
    const max = this.product()?.quantity ?? 1;
    this.quantity = Math.min(max, Math.max(1, this.quantity + delta));
  }
}
