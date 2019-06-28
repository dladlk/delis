import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { TokenService } from '../../service/system/token.service';
import { AuthorizationService } from '../../service/system/authorization.service';
import { LocaleService } from '../../service/system/locale.service';
import { ContentSelectInfoService } from '../../service/system/content-select.info.service';
import { LoginModel } from '../../model/system/login.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  login = '';
  password = '';

  message: string;
  errorStatus: boolean;

  constructor(
    private tokenService: TokenService,
    private auth: AuthorizationService,
    private translate: TranslateService,
    private locale: LocaleService,
    private contentSelectInfoService: ContentSelectInfoService,
    public router: Router) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.errorStatus = false;
  }

  ngOnInit() { }

  onLoggedIn() {
    if (this.login.length === 0 && this.password.length === 0) {
      this.message = 'login.error.username-or-password';
      this.errorStatus = true;
      return;
    }
    if (this.login.length === 0) {
      this.message = 'login.error.username';
      this.errorStatus = true;
      return;
    }
    if (this.password.length === 0) {
      this.message = 'login.error.password';
      this.errorStatus = true;
      return;
    }
    this.auth.login(this.login, this.password).subscribe(
      (data: {}) => {
        var loginModel: LoginModel = data['data'];
        this.tokenService.setToken(loginModel.accessToken);
        localStorage.setItem('username', loginModel.username);
        localStorage.setItem('refreshToken', loginModel.refreshToken);
        this.contentSelectInfoService.generateAllContentSelectInfo(loginModel.accessToken);
        this.contentSelectInfoService.generateUniqueOrganizationNameInfo(loginModel.accessToken);
        this.errorStatus = false;
        this.router.navigate(['']);
      }, error => {
        this.errorStatus = true;
        if (error.status === 0) {
          this.message = 'login.error.disconnect';
        }
        if (error.status === 401) {
          this.message = 'login.error.unauthorized';
        }
        this.router.navigate(['login']);
      }
    );
  }
}
