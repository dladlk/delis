import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { LocaleService } from "./locale.service";
import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime.config.service";
import { Router } from "@angular/router";
import { ListenErrorService } from "./listen.error.service";
import { ErrorModel } from "../models/error.model";

@Injectable()
export class ErrorService {

    constructor(
        private http: HttpRestService,
        private localeService: LocaleService,
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private listenErrorService: ListenErrorService,
        private router: Router) {
    }

    errorProcess(error: any) {

        switch (String(error["status"])) {
            case "401" : {
                this.logout();
            } break;
            default : {
                let listenError = new ErrorModel();
                listenError.status = String(error["status"]);
                let errorMessage = '';
                if (error["message"]) {
                    errorMessage += error["message"];
                }
                listenError.message = errorMessage;
                this.listenErrorService.loadError(listenError);
            }
        }
    }

    logout() {
        this.tokenService.resetToken();
        this.configService.resetConfigUrl();
        this.configService.resetCurrentUser();
        this.router.navigate(['/login']);
    }
}
