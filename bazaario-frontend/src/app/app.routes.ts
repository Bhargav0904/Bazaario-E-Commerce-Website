import { Routes } from '@angular/router';

import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { ProductList } from './pages/product-list/product-list';
import { ProductDetail } from './pages/product-detail/product-detail';
import { Cart } from './pages/cart/cart';
import { Checkout } from './pages/checkout/checkout';
import { Orders } from './pages/orders/orders';
import { Profile } from './pages/profile/profile';
import { AdminProducts } from './pages/admin-products/admin-products';
import { AdminOrders } from './pages/admin-orders/admin-orders';
import { AdminUsers } from './pages/admin-users/admin-users';

import { authGuard } from './core/auth-guard';
import { adminGuard } from './core/admin-guard';

export const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },

  // Public
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'products', component: ProductList },
  { path: 'products/:id', component: ProductDetail },

  // Logged-in users only
  { path: 'cart', component: Cart, canActivate: [authGuard] },
  { path: 'checkout', component: Checkout, canActivate: [authGuard] },
  { path: 'orders', component: Orders, canActivate: [authGuard] },
  { path: 'profile', component: Profile, canActivate: [authGuard] },

  // Admins only
  { path: 'admin/products', component: AdminProducts, canActivate: [adminGuard] },
  { path: 'admin/orders', component: AdminOrders, canActivate: [adminGuard] },
  { path: 'admin/users', component: AdminUsers, canActivate: [adminGuard] },

  // Anything else
  { path: '**', redirectTo: '' },
];
