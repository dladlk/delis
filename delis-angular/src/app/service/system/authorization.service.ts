import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpRestService } from './http-rest.service';
import { LocaleService } from './locale.service';
import { TokenService } from './token.service';
import { RuntimeConfigService } from './runtime-config.service';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  private url: string;

  constructor(private http: HttpRestService,
              private localeService: LocaleService,
              private tokenService: TokenService,
              private configService: RuntimeConfigService) { }

  login(login: string, password: string): Observable<any> {
    this.url = this.configService.getConfigUrl();
    let params = new HttpParams();
    params = params.append('username', login);
    params = params.append('password', password);
    params = params.append('grant_type', 'password');
    return this.http.methodLogin(this.url + '/oauth/token', params);
  }
}
