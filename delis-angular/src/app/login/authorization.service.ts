import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TokenService } from '../service/token.service';
import { environment } from '../../environments/environment';
import { RuntimeConfigService } from "../service/runtime.config.service";

@Injectable()
export class AuthorizationService {

  private env = environment;
  private url = this.env.api_url + '/security/signin';
  private config: string;

  constructor(
    private client: HttpClient,
    private tokenService: TokenService, private configService: RuntimeConfigService) {

  }

  setLogin(token: string): void {
    this.tokenService.setToken(token);
  }

  login(login: string, password: string) {

    this.configService.getUrl();
    this.config = localStorage.getItem('url');
    console.log('url = ' + localStorage.getItem('url'));
    if (this.config !== '${API_URL}') {
      this.url = this.config + '/security/signin';
    }

    this.tokenService.authenticated(login, password, this.url).subscribe(
        (data: {}) => {
          let token = data["token"];
          this.setLogin(token);
        }
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
