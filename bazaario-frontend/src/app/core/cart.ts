import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { Cart as CartModel } from '../models/cart.model';
import { API_BASE } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  // Held as a signal so the header's cart badge updates the moment anything changes,
  // without every component re-fetching.
  cart = signal<CartModel | null>(null);

  constructor(private http: HttpClient) {}

  load(): Observable<CartModel> {
    return this.http
      .get<CartModel>(`${API_BASE}/api/cart`)
      .pipe(tap((cart) => this.cart.set(cart)));
  }

  addItem(productId: number, quantity: number): Observable<CartModel> {
    return this.http
      .post<CartModel>(`${API_BASE}/api/cart/items`, { productId, quantity })
      .pipe(tap((cart) => this.cart.set(cart)));
  }

  updateQuantity(cartItemId: number, quantity: number): Observable<CartModel> {
    return this.http
      .put<CartModel>(`${API_BASE}/api/cart/items/${cartItemId}`, { quantity })
      .pipe(tap((cart) => this.cart.set(cart)));
  }

  removeItem(cartItemId: number): Observable<CartModel> {
    return this.http
      .delete<CartModel>(`${API_BASE}/api/cart/items/${cartItemId}`)
      .pipe(tap((cart) => this.cart.set(cart)));
  }

  clear(): Observable<void> {
    return this.http
      .delete<void>(`${API_BASE}/api/cart`)
      .pipe(tap(() => this.cart.set(null)));
  }

  itemCount(): number {
    return this.cart()?.itemCount ?? 0;
  }
}
