import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../core/auth';
import { CartService } from '../../core/cart';
import { CategoryService } from '../../core/category';
import { ProductService } from '../../core/product';
import { API_BASE } from '../../core/api.config';
import { Category } from '../../models/category.model';
import { Product } from '../../models/product.model';
import { HeroSlider } from '../../shared/hero-slider/hero-slider';

@Component({
  selector: 'app-home',
  imports: [RouterLink, HeroSlider],
  templateUrl: './home.html',
  styleUrl: './home.css',
})

export class Home implements OnInit {
  readonly apiBase = API_BASE;

  // Emoji for a category circle: the first keyword found in the category name wins
  private readonly icons: [string, string][] = [
    ['electron', '💻'], ['cloth', '👕'], ['fashion', '👕'], ['house', '🛋️'], ['home', '🛋️'],
    ['kitchen', '🍳'], ['sport', '👟'], ['fitness', '👟'], ['book', '📚'], ['beauty', '💄'], ['toy', '🧸'],
  ];

  categories = signal<Category[]>([]);
  shownCategories = computed(() => this.categories().slice(0, 6));

  featured = signal<Product[]>([]);
  loading = signal(true);
  failed = signal<ReadonlySet<number>>(new Set()); // products whose image file could not load
  addedId = signal<number | null>(null);
  cartError = signal<string | null>(null);

  newsMessage = signal<string | null>(null);
  newsOk = signal(false);

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private cartService: CartService,
    private auth: Auth,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({ next: (c) => this.categories.set(c) });
    this.productService.getAll().subscribe({
      next: (data) => {
        this.featured.set(data.slice(0, 8));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  // ---------- categories ----------
  iconFor(name: string): string {
    const lower = name.toLowerCase();
    return this.icons.find(([keyword]) => lower.includes(keyword))?.[1] ?? '🛍️';
  }

  // ---------- products ----------
  markFailed(productId: number): void {
    this.failed.update((set) => new Set(set).add(productId));
  }

  addToCart(product: Product): void {
    this.cartError.set(null);

    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    this.cartService.addItem(product.productId, 1).subscribe({
      next: () => {
        this.addedId.set(product.productId);
        setTimeout(() => this.addedId.set(null), 1000);
      },
      error: (err) => this.cartError.set(err.error?.message ?? 'Could not add to cart'),
    });
  }

  // ---------- newsletter (UI only: nothing is stored on the backend yet) ----------
  subscribe(event: Event, input: HTMLInputElement): void {
    event.preventDefault();
    const valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(input.value.trim());
    this.newsOk.set(valid);
    this.newsMessage.set(valid ? 'Thanks for subscribing!' : 'Please enter a valid email address.');
    if (valid) input.value = '';
  }
}
