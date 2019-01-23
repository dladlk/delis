import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { LocaleService } from "./locale.service";
import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime.config.service";
import { Router } from "@angular/router";

@Injectable()
export class ErrorService {

    constructor(
        private http: HttpRestService,
        private localeService: LocaleService,
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private router: Router) {
    }

    errorProcess(error: any) {

        switch (String(error["status"])) {
            case "401" : {
                this.logout();
            } break;
        }
    }

    logout() {
        location.reload();
        localStorage.removeItem('isLoggedin');
        this.tokenService.resetToken();
        this.configService.resetConfigUrl();
        this.configService.resetCurrentUser();
        this.localeService.resetLocale();
        this.router.navigate(['/login']);
    }
}
