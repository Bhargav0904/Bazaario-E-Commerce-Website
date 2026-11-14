import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { OrderService } from '../../core/order';
import { Order, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-admin-orders',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-orders.html',
  styleUrl: './admin-orders.css',
})
export class AdminOrders implements OnInit {
  orders = signal<Order[]>([]);
  message = signal<string | null>(null);
  error = signal<string | null>(null);
  loading = signal(true);

  readonly statuses: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.orderService.allOrders().subscribe({
      next: (data) => {
        this.orders.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  changeStatus(order: Order, status: string): void {
    this.message.set(null);
    this.error.set(null);

    // Cancelling restocks the items server-side, so warn before doing it
    if (status === 'CANCELLED' && !confirm(`Cancel order #${order.orderId}? Stock will be returned.`)) {
      this.reload();
      return;
    }

    this.orderService.updateStatus(order.orderId, status as OrderStatus).subscribe({
      next: () => {
        this.message.set(`Order #${order.orderId} set to ${status}`);
        this.reload();
      },
      error: (err) => this.error.set(err.error?.message ?? 'Could not update status'),
    });
  }

  badgeClass(status: string): string {
    return 'badge badge-' + status.toLowerCase();
  }
}
