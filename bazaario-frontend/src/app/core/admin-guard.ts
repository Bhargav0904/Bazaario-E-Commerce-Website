import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { Auth } from './auth';

// Equivalent of AuthorizationFilter - admin-only routes.
export const adminGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isAdmin()) {
    return true;
  }

  router.navigate(['/products']);
  return false;
};
