import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {routerTransition} from '../router.animations';
import {AuthorizationService} from './authorization.service';
import {LocaleService} from '../service/locale.service';
import {ContentSelectInfoService} from '../service/content.select.info.service';
import {TokenService} from '../service/token.service';
import {LoginData} from './login.data';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    animations: [routerTransition()]
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
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.errorStatus = false;
    }

    ngOnInit() {
    }

    onLoggedin() {
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
                const loginData: LoginData = data['data'];
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
