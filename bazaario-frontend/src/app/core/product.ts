import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Product, ProductRequest } from '../models/product.model';
import { API_BASE } from './api.config';

const API_URL = `${API_BASE}/api/products`;
const ADMIN_API_URL = `${API_BASE}/api/admin/products`;

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  constructor(private http: HttpClient) {}

  getAll(keyword?: string, categoryId?: number): Observable<Product[]> {
    let params = new HttpParams();
    if (keyword) params = params.set('keyword', keyword);
    if (categoryId) params = params.set('categoryId', categoryId);
    return this.http.get<Product[]>(API_URL, { params });
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${API_URL}/${id}`);
  }

  add(request: ProductRequest): Observable<Product> {
    return this.http.post<Product>(ADMIN_API_URL, request);
  }

  update(id: number, request: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${ADMIN_API_URL}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${ADMIN_API_URL}/${id}`);
  }
}
