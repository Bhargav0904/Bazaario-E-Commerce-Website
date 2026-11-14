import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { CartService } from '../../core/cart';
import { OrderService } from '../../core/order';
import { UserService } from '../../core/user';
import { PaymentMethod } from '../../models/order.model';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css',
})
export class Checkout implements OnInit {
  shippingAddress = '';
  paymentMethod: PaymentMethod = 'UPI';
  placing = signal(false);
  error = signal<string | null>(null);

  // Two-step flow, same as the backend: placeOrder() creates a PENDING order,
  // then pay() records the payment and flips it to CONFIRMED.
  placedOrderId = signal<number | null>(null);

  constructor(
    public cartService: CartService,
    private orderService: OrderService,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.cartService.load().subscribe();
    // Pre-fill with the address on their profile, since that's usually what they want
    this.userService.getProfile().subscribe({
      next: (profile) => (this.shippingAddress = profile.address ?? ''),
      error: () => {},
    });
  }

  placeOrder(): void {
    this.error.set(null);
    this.placing.set(true);

    this.orderService.placeOrder(this.shippingAddress).subscribe({
      next: (order) => {
        this.placedOrderId.set(order.orderId);
        this.placing.set(false);
        this.cartService.load().subscribe(); // cart is cleared server-side
      },
      error: (err) => {
        this.error.set(err.error?.message ?? 'Could not place your order');
        this.placing.set(false);
      },
    });
  }

  pay(): void {
    const orderId = this.placedOrderId();
    if (orderId === null) return;

    this.error.set(null);
    this.placing.set(true);

    // transactionId is left blank here - a real gateway (Razorpay etc.) would supply it
    // after the user completes payment in their widget.
    this.orderService.pay(orderId, this.paymentMethod).subscribe({
      next: () => this.router.navigate(['/orders']),
      error: (err) => {
        this.error.set(err.error?.message ?? 'Payment failed');
        this.placing.set(false);
      },
    });
  }
}
