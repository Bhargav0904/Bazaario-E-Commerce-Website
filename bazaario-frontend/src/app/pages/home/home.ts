import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../core/auth';
import { CartService } from '../../core/cart';
import { CategoryService } from '../../core/category';
import { ProductService } from '../../core/product';
import { API_BASE } from '../../core/api.config';
import { Category } from '../../models/category.model';
import { Product } from '../../models/product.model';

interface Slide {
  title: string; // "\n" starts a new line (home.css uses white-space: pre-line)
  text: string;
  art: string;
}

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  readonly apiBase = API_BASE;

  // ===== Edit these to change the hero banner =====
  readonly slides: Slide[] = [
    { title: 'Upgrade\nYour Everyday', text: 'Great products. Fair prices.', art: '🎧⌚📱' },
    { title: 'New Season\nNew Style', text: 'Fresh picks, just added.', art: '👕👟🎒' },
    { title: 'Make Home\nFeel Better', text: 'Small upgrades for every room.', art: '🛋️🍳🪴' },
  ];

  // Emoji for a category circle: the first keyword found in the category name wins
  private readonly icons: [string, string][] = [
    ['electron', '💻'], ['cloth', '👕'], ['fashion', '👕'], ['house', '🛋️'], ['home', '🛋️'],
    ['kitchen', '🍳'], ['sport', '👟'], ['fitness', '👟'], ['book', '📚'], ['beauty', '💄'], ['toy', '🧸'],
  ];

  index = signal(0);
  slide = computed(() => this.slides[this.index()]);

  categories = signal<Category[]>([]);
  shownCategories = computed(() => this.categories().slice(0, 6));

  featured = signal<Product[]>([]);
  loading = signal(true);
  failed = signal<ReadonlySet<number>>(new Set()); // products whose image file could not load
  addedId = signal<number | null>(null);
  cartError = signal<string | null>(null);

  newsMessage = signal<string | null>(null);
  newsOk = signal(false);

  private timer: ReturnType<typeof setInterval> | null = null;

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
    this.startAutoPlay();
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }

  // ---------- hero slider ----------
  goTo(i: number): void {
    this.index.set(i);
    this.startAutoPlay(); // restart the timer after a manual click
  }

  private startAutoPlay(): void {
    if (this.timer) clearInterval(this.timer);
    // Skip auto-play for people whose device asks for reduced motion
    if (window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) return;
    this.timer = setInterval(() => this.index.set((this.index() + 1) % this.slides.length), 4500);
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
