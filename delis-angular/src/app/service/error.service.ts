import { Injectable } from "@angular/core";
import { Router } from "@angular/router";

import { HttpRestService } from "./http.rest.service";
import { ListenErrorService } from "./listen.error.service";
import { ErrorModel } from "../models/error.model";
import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime.config.service";

@Injectable()
export class ErrorService {

    constructor(
        private tokenService: TokenService,
        private router: Router,
        private configService: RuntimeConfigService,
        private http: HttpRestService,
        private listenErrorService: ListenErrorService) {
    }

    errorProcess(error: any) {

        switch (String(error["status"])) {
            case "401" : {
                this.tokenService.resetToken();
                this.configService.resetCurrentUser();
                this.router.navigate(['/login']);
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
}
