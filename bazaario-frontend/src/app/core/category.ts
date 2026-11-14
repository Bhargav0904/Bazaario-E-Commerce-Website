import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Category } from '../models/category.model';
import { API_BASE } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(`${API_BASE}/api/categories`);
  }

  add(categoryName: string, description: string): Observable<Category> {
    return this.http.post<Category>(`${API_BASE}/api/admin/categories`, { categoryName, description });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/admin/categories/${id}`);
  }
}
