import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ChangePasswordRequest, UpdateProfileRequest, UserProfile } from '../models/user.model';
import { API_BASE } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${API_BASE}/api/profile`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${API_BASE}/api/profile`, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${API_BASE}/api/profile/password`, request);
  }

  // --- Admin ---

  allUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${API_BASE}/api/admin/users`);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE}/api/admin/users/${id}`);
  }
}
