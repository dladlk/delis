import { Injectable } from '@angular/core';
import { HttpRestService } from './http-rest.service';
import { LocaleService } from './locale.service';
import { TokenService } from './token.service';
import { RuntimeConfigService } from './runtime-config.service';
import { Observable } from 'rxjs';

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
    return this.http.methodPost(this.url + '/oauth/token?grant_type=password&username=' + login + '&password=' + password);
  }
}
