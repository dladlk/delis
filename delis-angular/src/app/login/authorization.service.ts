import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { TokenService } from '../service/token.service';
import { RuntimeConfigService } from "../service/runtime.config.service";
import { LocaleService } from "../service/locale.service";
import { HttpRestService } from "../service/http.rest.service";

@Injectable()
export class AuthorizationService {

    private url: string;

    constructor(
        private http: HttpRestService,
        private localeService: LocaleService,
        private tokenService: TokenService,
        private configService: RuntimeConfigService) {
    }

    login(login: string, password: string) : Observable<any> {
        this.url = this.configService.getConfigUrl();
        return this.http.methodPost(this.url + '/oauth/token?grant_type=password&username=' + login + '&password=' + password);
    }
}
