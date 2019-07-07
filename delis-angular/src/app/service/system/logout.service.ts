import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { TokenService } from "./token.service";
import { HttpRestService } from "./http-rest.service";
import { RuntimeConfigService } from "./runtime-config.service";

@Injectable({
    providedIn: 'root'
})
export class LogoutService {

    constructor(
        private tokenService: TokenService,
        private http: HttpRestService,
        private router: Router, private configService: RuntimeConfigService) { }

    logout() {
        this.http.methodDelete(this.configService.getConfigUrl() + '/rest/logout', this.tokenService.getToken()).subscribe(
            (data: {}) => {
                console.log('logout : ' + data['data']);
            }
        );
        this.tokenService.resetToken();
        this.configService.resetCurrentUser();
        this.router.navigate(['/login']);
    }
}
