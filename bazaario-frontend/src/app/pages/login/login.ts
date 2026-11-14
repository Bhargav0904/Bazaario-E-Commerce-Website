import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../core/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  errorMessage = signal<string | null>(null);

  constructor(private auth: Auth, private router: Router) {}

  onSubmit(): void {
    this.errorMessage.set(null);

    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigateByUrl('/products'),
      error: (err) => {
        this.errorMessage.set(err.error?.message ?? 'Invalid email or password');
      },
    });
  }
}
