import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {RuntimeConfigService} from "./runtime.config.service";

@Injectable()
export class TokenService {

  private TOKEN_KEY = 'token';
  private headers: HttpHeaders;

  constructor(
      private client: HttpClient, private configService: RuntimeConfigService) {
    this.headers = new HttpHeaders({
      'Content-Type' : `application/json`
    });
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

  authenticated(login: string, password: string, url: string) : Observable<any> {
    return this.client
        .post(url, {
          'login' : login,
          'password' : password,
        }, {
          headers : this.headers
        })
        .pipe(
            map(TokenService.extractData)
        );
  }

  private static extractData(res: Response) {
    return res || { };
  }
}
