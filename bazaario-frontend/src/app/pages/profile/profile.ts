import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UserService } from '../../core/user';
import { UserProfile } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profile = signal<UserProfile | null>(null);
  fullName = '';
  phone = '';
  address = '';

  oldPassword = '';
  newPassword = '';

  profileMessage = signal<string | null>(null);
  profileError = signal<string | null>(null);
  passwordMessage = signal<string | null>(null);
  passwordError = signal<string | null>(null);

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (p) => {
        this.profile.set(p);
        this.fullName = p.fullName;
        this.phone = p.phone ?? '';
        this.address = p.address ?? '';
      },
    });
  }

  saveProfile(): void {
    this.profileMessage.set(null);
    this.profileError.set(null);

    this.userService
      .updateProfile({ fullName: this.fullName, phone: this.phone, address: this.address })
      .subscribe({
        next: (p) => {
          this.profile.set(p);
          this.profileMessage.set('Profile updated');
        },
        error: (err) => this.profileError.set(err.error?.message ?? 'Could not update profile'),
      });
  }

  changePassword(): void {
    this.passwordMessage.set(null);
    this.passwordError.set(null);

    this.userService
      .changePassword({ oldPassword: this.oldPassword, newPassword: this.newPassword })
      .subscribe({
        next: (res) => {
          this.passwordMessage.set(res.message);
          this.oldPassword = '';
          this.newPassword = '';
        },
        error: (err) => this.passwordError.set(err.error?.message ?? 'Could not change password'),
      });
  }
}
