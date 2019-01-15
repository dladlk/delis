import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TokenService } from '../service/token.service';
import { environment } from '../../environments/environment';

@Injectable()
export class AuthorizationService {

  private headers: HttpHeaders;
  private env = environment;
  private url = this.env.api_url + '/security/signin';

  constructor(
    private client: HttpClient,
    private tokenService: TokenService) {
    this.headers = new HttpHeaders({
      'Content-Type' : `application/json`
    });
  }

  setLogin(res: any): any {
    this.tokenService.setToken(res.data);
    return res;
  }

  login(login: string, password: string): Observable<Response> {
    return this.client
      .post(this.url, {
        'login' : login,
        'password' : password,
      }, {
        headers : this.headers
      })
      .pipe(
        map((res: Response) => {
          return this.setLogin(res) || { };
        })
      );
  }

  logout() {
    location.reload();
    this.tokenService.resetToken();
  }

  isAuthenticated(): boolean {
    return !! this.tokenService.getToken();
  }
}
