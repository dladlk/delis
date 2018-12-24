import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TokenService } from '../service/token.service';
import { environment } from '../../environments/environment';
import { IUser, ROLES } from '../models/user.model';
import { base_url } from '../app.constants';
import {IResult} from "../models/result.model";

@Injectable()
export class AuthorizationService {

  private headers: HttpHeaders;
  private user: IUser;
  private ROLES = ROLES;

  constructor(
    private client: HttpClient,
    private tokenService: TokenService) {
    this.headers = new HttpHeaders({
      'Authorization' : `Basic ${btoa(environment.credentials)}`
    });
  }

  setLogin(res: any): IResult<any> {
    console.log('res = ' + res.toString());
    this.tokenService.setToken(res.access_token);
    return res;
  }

  login(email: string, password: string): Observable<IResult<any>> {
    console.log('email = ' + email);
    console.log('password = ' + password);
    return this.client
      .post(`${base_url}/oauth/token`, {}, {
        headers : this.headers,
        params : {email, password, grant_type : 'password'}
      })
      .pipe(
        map((res: any) => this.setLogin(res))
      );
  }

  logout() {
    location.reload();
    this.tokenService.resetToken();
  }

  isAuthenticated(): boolean {
    return !!this.tokenService.getToken();
  }

  hasPermissions(roles: string[] = [], onlyThis: boolean = false) {
    if (!this.user) {
      return false;
    }
    if (roles.indexOf('*') >= 0) {
      return true;
    }
    if (this.user.role === this.ROLES.ADMIN && !onlyThis) {
      return true;
    }
    if (roles.indexOf(this.user.role) >= 0) {
      return true;
    }
  }
}
