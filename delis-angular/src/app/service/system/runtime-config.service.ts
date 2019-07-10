import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpRestService } from './http-rest.service';
import { LoginModel } from "../../model/system/login.model";
import { CurrentUserModel } from "../../model/system/current-user.model";

@Injectable({
  providedIn: 'root'
})
export class RuntimeConfigService {

  private env = environment;
  private url = this.env.api_url;
  private config: string;
  private LOCALE_URL = 'url';
  private CURRENT_USER = 'current_user';

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

  setCurrentUser(loginData: LoginModel) {
    localStorage.setItem(this.CURRENT_USER, JSON.stringify(new CurrentUserModel(loginData)));
  }

  getCurrentUser(): CurrentUserModel {
    return JSON.parse(localStorage.getItem(this.CURRENT_USER));
  }

  resetCurrentUser() {
    localStorage.removeItem(this.CURRENT_USER);
  }
}
