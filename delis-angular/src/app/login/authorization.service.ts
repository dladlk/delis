import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TokenService } from '../service/token.service';
import { environment } from '../../environments/environment';
import { RuntimeConfigService } from "../service/runtime.config.service";
import { AlertService } from "../alert/alert.service";

@Injectable()
export class AuthorizationService {

  private env = environment;
  private url = this.env.api_url + '/security/signin';
  private config: string;

  constructor(
    private client: HttpClient,
    private alertService: AlertService,
    private tokenService: TokenService,
    private configService: RuntimeConfigService) {

  }

  setLogin(token: string): void {
    this.tokenService.setToken(token);
  }

  login(login: string, password: string) {

    this.configService.getUrl();
    this.config = localStorage.getItem('url');
    if (this.config !== '${API_URL}') {
      this.url = this.config + '/security/signin';
    }

    this.tokenService.authenticated(login, password, this.url).subscribe(
        (data: {}) => {
          this.setLogin(data["data"]);
        }, error => {
          this.alertService.error(error);
        }
    );
  }

  logout() {
    location.reload();
    this.tokenService.resetToken();
  }
}
