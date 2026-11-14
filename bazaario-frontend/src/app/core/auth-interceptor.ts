import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { Auth } from './auth';

// Functional interceptor (current Angular style, replaces the old class-based
// HTTP_INTERCEPTORS pattern) - attaches the JWT to every outgoing request automatically,
// so individual services (ProductService, etc.) never need to touch the token directly.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);
  const token = auth.getToken();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req);
};
