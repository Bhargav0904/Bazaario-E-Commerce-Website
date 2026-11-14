import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { Auth } from '../../core/auth';
import { RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  form: RegisterRequest = { fullName: '', email: '', password: '', phone: '', address: '' };
  errorMessage = signal<string | null>(null);

  constructor(private auth: Auth, private router: Router) {}

  onSubmit(): void {
    this.errorMessage.set(null);

    this.auth.register(this.form).subscribe({
      next: () => this.router.navigateByUrl('/products'),
      error: (err) => {
        // Field-level validation errors come back as { fullName: "...", email: "..." } -
        // fall back to a generic message for anything else (e.g. duplicate email).
        const body = err.error;
        const message =
          typeof body === 'object' && body !== null && !body.message
            ? Object.values(body).join(', ')
            : body?.message ?? 'Registration failed';
        this.errorMessage.set(message);
      },
    });
  }
}
