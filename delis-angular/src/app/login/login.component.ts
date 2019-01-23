import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { routerTransition } from '../router.animations';
import { AuthorizationService } from './authorization.service';
import { LocaleService } from "../service/locale.service";
import { environment } from "../../environments/environment";
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

    private env = environment;

    login: string;
    password: string;

    error: boolean;

    constructor(
        private tokenService: TokenService,
        private auth: AuthorizationService,
        private translate: TranslateService,
        private locale: LocaleService,
        private contentSelectInfoService: ContentSelectInfoService,
        public router: Router) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {
    }

    onLoggedin() {
        if (this.env.production) {
            this.auth.login(this.login, this.password).subscribe(
                (data: {}) => {
                    let loginData: LoginData = data["data"];
                    this.tokenService.setToken(loginData.token);
                    localStorage.setItem('username', loginData.username);
                    this.contentSelectInfoService.generateAllContentSelectInfo();
                    localStorage.setItem('isLoggedin', 'true');
                    this.router.navigate(['/dashboard']);
                }, error => {
                    this.error = true;
                    this.router.navigate(['/login']);
                }
            );
        } else {
            localStorage.setItem('isLoggedin', 'true');
            this.router.navigate(['/dashboard']);
        }
    }
}
