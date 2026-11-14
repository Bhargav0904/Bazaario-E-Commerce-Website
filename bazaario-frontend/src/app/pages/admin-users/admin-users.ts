import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserService } from '../../core/user';
import { Auth } from '../../core/auth';
import { UserProfile } from '../../models/user.model';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers implements OnInit {
  users = signal<UserProfile[]>([]);
  message = signal<string | null>(null);
  error = signal<string | null>(null);
  loading = signal(true);

  constructor(private userService: UserService, private auth: Auth) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.userService.allUsers().subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  // Don't offer a delete button for yourself - locking yourself out of the admin
  // panel mid-session is an easy mistake to make.
  isSelf(user: UserProfile): boolean {
    return this.auth.currentUser()?.userId === user.userId;
  }

  remove(user: UserProfile): void {
    if (!confirm(`Delete ${user.fullName}? This can't be undone.`)) return;

    this.message.set(null);
    this.error.set(null);

    this.userService.deleteUser(user.userId).subscribe({
      next: () => {
        this.message.set(`${user.fullName} deleted`);
        this.reload();
      },
      // The backend refuses to delete a user who has orders - surface that reason
      error: (err) => this.error.set(err.error?.message ?? 'Could not delete user'),
    });
  }
}
