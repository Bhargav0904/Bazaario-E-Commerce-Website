import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ProductService } from '../../core/product';
import { CategoryService } from '../../core/category';
import { Product, ProductRequest } from '../../models/product.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-admin-products',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css',
})
export class AdminProducts implements OnInit {
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  message = signal<string | null>(null);
  error = signal<string | null>(null);

  // null = the form is in "add" mode; a number = editing that product
  editingId = signal<number | null>(null);

  form: ProductRequest = {
    productName: '',
    description: '',
    price: 0,
    quantity: 0,
    categoryId: 0,
    imageUrl: '',
  };

  constructor(private productService: ProductService, private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.reload();
    this.categoryService.getAll().subscribe({
      next: (cats) => {
        this.categories.set(cats);
        if (cats.length > 0 && this.form.categoryId === 0) {
          this.form.categoryId = cats[0].categoryId;
        }
      },
    });
  }

  reload(): void {
    this.productService.getAll().subscribe({ next: (data) => this.products.set(data) });
  }

  startEdit(product: Product): void {
    this.editingId.set(product.productId);
    this.form = {
      productName: product.productName,
      description: product.description,
      price: product.price,
      quantity: product.quantity,
      categoryId: product.categoryId,
      imageUrl: product.imageUrl ?? '',
    };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.resetForm();
  }

  save(): void {
    this.message.set(null);
    this.error.set(null);

    const editing = this.editingId();
    const request$ = editing
      ? this.productService.update(editing, this.form)
      : this.productService.add(this.form);

    request$.subscribe({
      next: () => {
        this.message.set(editing ? 'Product updated' : 'Product added');
        this.editingId.set(null);
        this.resetForm();
        this.reload();
      },
      error: (err) => this.error.set(this.readError(err)),
    });
  }

  remove(product: Product): void {
    if (!confirm(`Delete "${product.productName}"? This can't be undone.`)) return;

    this.productService.delete(product.productId).subscribe({
      next: () => {
        this.message.set('Product deleted');
        this.reload();
      },
      error: (err) => this.error.set(this.readError(err)),
    });
  }

  private resetForm(): void {
    const firstCategory = this.categories()[0]?.categoryId ?? 0;
    this.form = {
      productName: '',
      description: '',
      price: 0,
      quantity: 0,
      categoryId: firstCategory,
      imageUrl: '',
    };
  }

  // Validation failures come back as a field->message map; everything else has .message
  private readError(err: any): string {
    const body = err.error;
    if (body && typeof body === 'object' && !body.message) {
      return Object.values(body).join(', ');
    }
    return body?.message ?? 'Something went wrong';
  }
}
