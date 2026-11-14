import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { vi } from 'vitest';

import { Home } from './home';

const categories = [
  { categoryId: 1, categoryName: 'Electronics', description: '' },
  { categoryId: 2, categoryName: 'Books', description: '' },
];
const products = [
  { productId: 1, productName: 'Keyboard', description: '', price: 1199.99, quantity: 18, categoryId: 1, categoryName: 'Electronics', imageUrl: null },
  { productId: 2, productName: 'Novel', description: '', price: 299, quantity: 0, categoryId: 2, categoryName: 'Books', imageUrl: null },
];

describe('Home', () => {
  let http: HttpTestingController;

  async function loadedPage() {
    const fixture = TestBed.createComponent(Home);
    fixture.detectChanges();
    http.expectOne((r) => r.url.endsWith('/api/categories')).flush(categories);
    http.expectOne((r) => r.url.endsWith('/api/products')).flush(products);
    await fixture.whenStable();
    fixture.detectChanges();
    return fixture;
  }

  beforeEach(async () => {
    localStorage.clear(); // start logged out
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  it('shows real categories and products from the backend', async () => {
    const el = (await loadedPage()).nativeElement as HTMLElement;
    expect(el.querySelectorAll('.cat').length).toBe(3); // 2 categories + the "More" circle
    expect(el.querySelectorAll('.card').length).toBe(2);
    expect(el.querySelector('.price')?.textContent).toContain('1199.99');
    expect(el.querySelectorAll('.card .add')[1].hasAttribute('disabled')).toBe(true); // out of stock
  });

  it('changes the hero slide when a dot is clicked', async () => {
    const fixture = await loadedPage();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('h1')?.textContent).toContain('Upgrade');
    (el.querySelectorAll('.dots button')[1] as HTMLButtonElement).click();
    fixture.detectChanges();
    expect(el.querySelector('h1')?.textContent).toContain('New Season');
  });

  it('sends a logged-out visitor to the login page instead of adding to cart', async () => {
    const nav = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const el = (await loadedPage()).nativeElement as HTMLElement;
    (el.querySelector('.card .add') as HTMLButtonElement).click();
    expect(nav).toHaveBeenCalledWith(['/login']);
  });

  it('validates the newsletter email', async () => {
    const fixture = await loadedPage();
    const el = fixture.nativeElement as HTMLElement;
    const input = el.querySelector('.news input') as HTMLInputElement;
    const form = el.querySelector('.news form') as HTMLFormElement;

    input.value = 'nope';
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(el.querySelector('.msg')?.textContent).toContain('valid email');

    input.value = 'me@example.com';
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(el.querySelector('.msg')?.textContent).toContain('Thanks');
  });
});
