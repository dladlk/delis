import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { routerTransition } from '../router.animations';
import { AuthorizationService } from './authorization.service';
import { LocaleService } from "../service/locale.service";
import { ContentSelectInfoService } from "../service/content.select.info.service";
import { TokenService } from "../service/token.service";
import { LoginData } from "./login.data";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    animations: [routerTransition()]
})
export class LoginComponent implements OnInit {

    login: string;
    password: string;

    message: string;
    errorStatus: boolean;

    constructor(
        private tokenService: TokenService,
        private auth: AuthorizationService,
        private translate: TranslateService,
        private locale: LocaleService,
        private contentSelectInfoService: ContentSelectInfoService,
        public router: Router) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.errorStatus = false;
    }

    ngOnInit() {
    }

    onLoggedin() {
        if (this.login === undefined && this.password === undefined) {
            this.message = "login and password can not be empty";
            this.errorStatus = true;
            return;
        }
        if (this.login === undefined) {
            this.message = "login can not be empty";
            this.errorStatus = true;
            return;
        }
        if (this.password === undefined) {
            this.message = "password can not be empty";
            this.errorStatus = true;
            return;
        }
        this.auth.login(this.login, this.password).subscribe(
            (data: {}) => {
                let loginData: LoginData = data["data"];
                this.tokenService.setToken(loginData.token);
                localStorage.setItem('username', loginData.username);
                this.contentSelectInfoService.generateAllContentSelectInfo();
                this.contentSelectInfoService.generateUniqueOrganizationNameInfo();
                this.contentSelectInfoService.generateDateRangeInfo();
                this.errorStatus = false;
                this.router.navigate(['/dashboard']);
            }, error => {
                console.log('error : ' + error.status);
                this.errorStatus = true;
                if (error.status === 0) {
                    this.message = "server disconnect";
                }
                if (error.status === 401) {
                    this.message = "invalid username or password";
                }
                this.router.navigate(['/login']);
            }
        );
    }
}
