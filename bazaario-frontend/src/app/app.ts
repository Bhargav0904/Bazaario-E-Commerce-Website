import { Component, OnInit, computed, signal } from '@angular/core';
import { Router, RouterOutlet, RouterLink } from '@angular/router';

import { Auth } from './core/auth';
import { CartService } from './core/cart';
import { CategoryService } from './core/category';
import { Category } from './models/category.model';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('bazaario-frontend');

  categories = signal<Category[]>([]);
  navCategories = computed(() => this.categories().slice(0, 7)); // how many show in the nav bar

  constructor(
    public auth: Auth,
    public cartService: CartService,
    private categoryService: CategoryService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    // Load the cart once on startup so the header badge is accurate after a refresh
    if (this.auth.isLoggedIn()) {
      this.cartService.load().subscribe({ error: () => {} });
    }
    this.categoryService.getAll().subscribe({ next: (c) => this.categories.set(c), error: () => {} });
  }

  // Header search: sends the text to the product list, which reads ?keyword=
  search(event: Event, text: string): void {
    event.preventDefault();
    const keyword = text.trim();
    this.router.navigate(['/products'], { queryParams: keyword ? { keyword } : {} });
  }

  firstName(): string {
    return this.auth.currentUser()?.fullName?.split(' ')[0] ?? 'Account';
  }

  logout(): void {
    this.auth.logout();
    this.cartService.cart.set(null);
  }
}