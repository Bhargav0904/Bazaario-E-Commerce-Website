import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { Auth } from './auth';

// Blocks routes that need a logged-in user (cart, checkout, orders, profile).
// This is the Angular-side equivalent of the JSP version's AuthenticationFilter -
// note the backend enforces the same rule independently, since a client-side guard
// alone is never a security boundary.
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
