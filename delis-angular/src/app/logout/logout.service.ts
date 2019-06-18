import { Injectable } from '@angular/core';
import { HttpRestService } from '../service/http.rest.service';
import { Router } from '@angular/router';
import { RuntimeConfigService } from '../service/runtime.config.service';
import { TokenService } from '../service/token.service';

@Injectable()
export class LogoutService {

    private url: string;

    constructor(
        private tokenService: TokenService,
        private http: HttpRestService,
        private router: Router,
        private configService: RuntimeConfigService) {}

    logout() {
        this.url = this.configService.getConfigUrl();
        this.http.methodDelete(this.url + '/rest/logout', this.tokenService.getToken()).subscribe(
            (data: {}) => {
                console.log('logout : ' + data['data']);
            }
        );
        this.tokenService.resetToken();
        this.configService.resetCurrentUser();
        this.router.navigate(['/login']);
    }
}
