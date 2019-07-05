import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
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

    errorStatus: boolean;
    message: string;

    public loginForm: FormGroup;

    constructor(
        private tokenService: TokenService,
        private auth: AuthorizationService,
        private translate: TranslateService,
        private locale: LocaleService,
        private contentSelectInfoService: ContentSelectInfoService,
        public router: Router) {
    }

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
                if (error.status === 500) {
                    this.message = 'login.error.disconnect';
                }
                this.router.navigate(['/login']);
            }
        );
    }

}
