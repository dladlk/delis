import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {LoginModel} from '../../model/system/login.model';
import {AuthorizationService} from '../../service/system/authorization.service';
import {TokenService} from '../../service/system/token.service';
import {CheckExpirationService} from '../../service/system/check-expiration.service';
import {RuntimeConfigService} from '../../service/system/runtime-config.service';
import {ContentSelectInfoService} from '../../service/system/content-select-info.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  errorStatus: boolean;
  message: string;

  public loginForm: FormGroup;

  constructor(
    private router: Router,
    private auth: AuthorizationService,
    private tokenService: TokenService,
    private checkExpirationService: CheckExpirationService,
    private runtimeConfigService: RuntimeConfigService,
    private contentSelectInfoService: ContentSelectInfoService) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });
  }

  public hasError = (controlName: string, errorName: string) => {
    return this.loginForm.controls[controlName].hasError(errorName);
  };

  public login = (loginValue) => {
    if (this.loginForm.valid) {
      this.executeLogin(loginValue);
    }
  };

  private executeLogin = (loginValue) => {

    this.auth.login(loginValue.username, loginValue.password).subscribe(
      (data: {}) => {
        const now = new Date();
        const loginData: LoginModel = data['data'];
        this.tokenService.setToken(loginData.accessToken);
        this.checkExpirationService.setExpiration(new Date(now.getTime() + (1000 * (loginData.expiration))));
        this.runtimeConfigService.setCurrentUser(loginData);
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
        if (error.status === 500) {
          this.message = 'login.error.disconnect';
        }
        this.router.navigate(['/login']);
      }
    );
  }
}
