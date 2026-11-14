import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Order, OrderStatus, Payment, PaymentMethod } from '../models/order.model';
import { API_BASE } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  constructor(private http: HttpClient) {}

  placeOrder(shippingAddress: string): Observable<Order> {
    return this.http.post<Order>(`${API_BASE}/api/orders`, { shippingAddress });
  }

  myOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_BASE}/api/orders`);
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${API_BASE}/api/orders/${id}`);
  }

  pay(orderId: number, paymentMethod: PaymentMethod, transactionId?: string): Observable<Payment> {
    return this.http.post<Payment>(`${API_BASE}/api/payments`, {
      orderId,
      paymentMethod,
      transactionId: transactionId ?? null,
    });
  }

  getPaymentForOrder(orderId: number): Observable<Payment> {
    return this.http.get<Payment>(`${API_BASE}/api/payments/order/${orderId}`);
  }

  // --- Admin ---

  allOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${API_BASE}/api/admin/orders`);
  }

  updateStatus(orderId: number, status: OrderStatus): Observable<Order> {
    return this.http.put<Order>(`${API_BASE}/api/admin/orders/${orderId}/status`, { status });
  }
}
