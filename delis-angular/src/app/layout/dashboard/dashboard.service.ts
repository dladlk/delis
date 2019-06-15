import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { TokenService } from "../../service/token.service";
import { RuntimeConfigService } from "../../service/runtime.config.service";
import { HttpRestService } from "../../service/http.rest.service";

@Injectable()
export class DashboardService {

    private url : string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/dashboard';
    }

    getDashboardModel() : Observable<any> {
        return this.httpRestService.methodGet(this.url, null, this.tokenService.getToken());
    }
}
