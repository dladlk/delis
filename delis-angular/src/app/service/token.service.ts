import { Injectable } from '@angular/core';

@Injectable()
export class TokenService {

  private TOKEN_KEY = 'token';

  constructor() {}

  static token() {
    return localStorage.getItem('token');
  }

  setToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  resetToken() {
    localStorage.removeItem(this.TOKEN_KEY);
  }
}
