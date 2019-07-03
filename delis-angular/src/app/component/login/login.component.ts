import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { LoginModel } from '../../model/system/login.model';
import { TokenService } from '../../service/system/token.service';
import { AuthorizationService } from '../../service/system/authorization.service';
import { LocaleService } from '../../service/system/locale.service';
import { ContentSelectInfoService } from '../../service/system/content-select-info.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  // @ViewChild('username', {static: true}) username: ElementRef;

  form: any = {};

  errorStatus: boolean;
  message: string;

  constructor(
    private tokenService: TokenService,
    private auth: AuthorizationService,
    private translate: TranslateService,
    private locale: LocaleService,
    private contentSelectInfoService: ContentSelectInfoService,
    public router: Router) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    // this.errorStatus = false;
    // setTimeout(() => {
    //   this.username.nativeElement.focus();
    // }, 0);
  }

  ngOnInit() {}

  login() {
    this.auth.login(this.form.username, this.form.password).subscribe(
      (data: {}) => {
        const loginData: LoginModel = data['data'];
        this.tokenService.setToken(loginData.accessToken);
        localStorage.setItem('username', loginData.username);
        localStorage.setItem('refreshToken', loginData.refreshToken);
        this.contentSelectInfoService.generateAllContentSelectInfo(loginData.accessToken);
        this.contentSelectInfoService.generateUniqueOrganizationNameInfo(loginData.accessToken);
        this.errorStatus = false;
        this.router.navigate(['/dashboard']);
      }, error => {
            this.errorStatus = true;
            if (error.status === 0) {
                this.message = 'login.error.disconnect';
            }
            if (error.status === 401) {
                this.message = 'login.error.unauthorized';
            }
        this.router.navigate(['/login']);
      }
    );
  }
}
