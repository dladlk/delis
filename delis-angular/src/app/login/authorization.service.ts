import { Injectable } from '@angular/core';
import { TokenService } from '../service/token.service';
import { RuntimeConfigService } from "../service/runtime.config.service";
import { AlertService } from "../alert/alert.service";
import { LocaleService } from "../service/locale.service";

@Injectable()
export class AuthorizationService {

    private url: string;

    constructor(
        private localeService: LocaleService,
        private alertService: AlertService,
        private tokenService: TokenService,
        private configService: RuntimeConfigService) {
    }

    login(login: string, password: string) {
        this.url = this.configService.getConfigUrl();
        this.tokenService.authenticated(login, password, this.url + '/security/signin');
    }

    logout() {
        location.reload();
        this.tokenService.resetToken();
        this.configService.resetConfigUrl();
        this.localeService.resetLocale();
    }
}
