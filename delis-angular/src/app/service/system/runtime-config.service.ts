import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpRestService } from './http-rest.service';

@Injectable({
  providedIn: 'root'
})
export class RuntimeConfigService {

  private env = environment;
  private url = this.env.api_url;
  private config: string;
  private LOCALE_URL = 'url';
  private LOCALE_USERNAME = 'username';
  private ROLE = 'role';

  constructor(private http: HttpRestService) { }

  getUrl() {
    this.http.methodInnerGet('assets/config/runtime.json').subscribe(
      (data: {}) => {
        localStorage.setItem(this.LOCALE_URL, data['PARAM_API_URL']);
      }
    );
  }

  getConfigUrl(): string {
    this.config = localStorage.getItem(this.LOCALE_URL);
    if (this.config === null || this.config === '${API_URL}') {
      return this.url;
    } else {
      return this.config;
    }
  }

  getCurrentUser(): string {
    return localStorage.getItem(this.LOCALE_USERNAME);
  }

  getRole(): string {
    return localStorage.getItem(this.ROLE);
  }

  resetCurrentUser() {
    localStorage.removeItem(this.LOCALE_USERNAME);
    localStorage.removeItem(this.ROLE);
  }
}
