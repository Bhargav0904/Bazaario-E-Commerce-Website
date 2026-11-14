import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { API_BASE } from './api.config';

const API_URL = `${API_BASE}/api/auth`;
const STORAGE_KEY = 'bazaario_auth';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  // A signal holding the logged-in user (or null) - components read this reactively
  // instead of manually re-checking localStorage everywhere.
  currentUser = signal<AuthResponse | null>(this.readFromStorage());

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/register`, request)
      .pipe(tap((response) => this.saveSession(response)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/login`, request)
      .pipe(tap((response) => this.saveSession(response)));
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null;
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    this.currentUser.set(response);
  }

  private readFromStorage(): AuthResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
