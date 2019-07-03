import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { RuntimeConfigService } from './runtime-config.service';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private TOKEN_KEY = 'token';
  private headers: HttpHeaders;
  private url: string;

  constructor( private http: HttpClient, private tokenService: TokenService, private configService: RuntimeConfigService ) {
    this.url = this.configService.getConfigUrl();
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

  isAuthenticated(): boolean {
    let token = this.getToken();
    if (token === null) {
      return false;
    }
    return !!this.getToken();
  }

  refreshTokenInit(token: string): Observable<any> {

    this.headers = new HttpHeaders({
      'Content-Type' : `application/json`,
      'Authorization' : `Basic dGVzdDp0ZXN0`
    });
    return this.http.post(this.url + "/oauth/token?grant_type=refresh_token&refresh_token=" + token, null, {
      headers : this.headers
    }).pipe(map(TokenService.extractData));

  }

  private static extractData(res: Response) {
    return res || { };
  }
}
