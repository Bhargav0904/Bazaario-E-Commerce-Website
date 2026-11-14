import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { OrderService } from '../../core/order';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, RouterLink],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class Orders implements OnInit {
  orders = signal<Order[]>([]);
  expandedId = signal<number | null>(null);
  loading = signal(true);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.myOrders().subscribe({
      next: (data) => {
        this.orders.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  toggle(orderId: number): void {
    this.expandedId.set(this.expandedId() === orderId ? null : orderId);
  }

  badgeClass(status: string): string {
    return 'badge badge-' + status.toLowerCase();
  }
}
