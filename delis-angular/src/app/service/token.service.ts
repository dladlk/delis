import { Injectable } from '@angular/core';
import { HttpRestService } from "./http.rest.service";

@Injectable()
export class TokenService {

  private TOKEN_KEY = 'token';

  constructor(
      private http: HttpRestService) {
  }

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

  authenticated(login: string, password: string, url: string) {
    let body  =  {
      'login' : login,
      'password' : password,
    };
    this.http.methodPost(url, body).subscribe(
        (data: {}) => {
          this.setToken(data["data"]);
        }
    );
  }
}
