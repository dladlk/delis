import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRestService } from './http-rest.service';
import { RuntimeConfigService } from './runtime-config.service';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  private url: string;

  constructor(private http: HttpRestService, private configService: RuntimeConfigService) { }

  login(login: string, password: string): Observable<any> {
    this.url = this.configService.getConfigUrl();
    return this.http.methodLogin(this.url + '/oauth/token', login, password);
  }
}
