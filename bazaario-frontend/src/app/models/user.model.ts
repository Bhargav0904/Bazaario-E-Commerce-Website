export interface AuthResponse {
  token: string;
  userId: number;
  fullName: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phone?: string;
  address?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UserProfile {
  userId: number;
  fullName: string;
  email: string;
  phone: string | null;
  address: string | null;
  role: 'CUSTOMER' | 'ADMIN';
  createdAt: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  phone?: string;
  address?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
