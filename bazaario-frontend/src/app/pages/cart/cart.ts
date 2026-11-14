import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { CartService } from '../../core/cart';
import { API_BASE } from '../../core/api.config';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart implements OnInit {
  readonly apiBase = API_BASE;
  error = signal<string | null>(null);
  loading = signal(true);

  constructor(public cartService: CartService) {}

  ngOnInit(): void {
    this.cartService.load().subscribe({
      next: () => this.loading.set(false),
      error: () => this.loading.set(false),
    });
  }

  updateQty(cartItemId: number, quantity: number): void {
    this.error.set(null);
    this.cartService.updateQuantity(cartItemId, quantity).subscribe({
      error: (err) => this.error.set(err.error?.message ?? 'Could not update quantity'),
    });
  }

  remove(cartItemId: number): void {
    this.cartService.removeItem(cartItemId).subscribe({
      error: (err) => this.error.set(err.error?.message ?? 'Could not remove item'),
    });
  }

  clear(): void {
    if (!confirm('Clear your entire cart?')) return;
    this.cartService.clear().subscribe({
      next: () => this.cartService.load().subscribe(),
    });
  }
}
