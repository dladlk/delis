import { Injectable } from '@angular/core';

@Injectable()
export class TokenService {

  private TOKEN_KEY = 'token';

  constructor() {}

  setToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  resetToken() {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  isAuthenticated() : boolean {
    return !!this.getToken();
  }
}
