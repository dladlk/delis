import { Injectable } from "@angular/core";
import { Router } from "@angular/router";

import { HttpRestService } from "./http.rest.service";
import { ListenErrorService } from "./listen.error.service";
import { ErrorModel } from "../models/error.model";
import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime.config.service";
import { RefreshTokenService } from "./refresh.token.service";
import { LoginData } from "../login/login.data";

@Injectable()
export class ErrorService {

    constructor(
        private tokenService: TokenService,
        private refreshTokenService: RefreshTokenService,
        private router: Router,
        private configService: RuntimeConfigService,
        private http: HttpRestService,
        private listenErrorService: ListenErrorService) {
    }

    errorProcess(error: any) {
        switch (String(error["status"])) {
            case '401' : {
                let errorToken: string = JSON.stringify(error.error.error);
                if (new String(JSON.parse(errorToken)).valueOf() == new String("invalid_token").valueOf()) {
                    this.refreshTokenService.refreshTokenInit(localStorage.getItem("refreshToken")).subscribe(
                        (data: {}) => {
                            let loginData: LoginData = data["data"];
                            this.tokenService.setToken(loginData.accessToken);
                            location.reload();
                        }, error => {
                            this.resetProcess();
                        }
                    );
                } else {
                    this.resetProcess();
                }
            } break;
            case '403' : {
                let listenError = new ErrorModel();
                listenError.status = String(error["status"]);
                listenError.message = error.error.fieldErrors[0].message;
                this.listenErrorService.loadError(listenError);
            } break;
            default : {
                let listenError = new ErrorModel();
                listenError.status = String(error["status"]);
                listenError.message = String(error["message"]);
                this.listenErrorService.loadError(listenError);
            }
        }
    }

    resetProcess() {
        this.tokenService.resetToken();
        localStorage.removeItem("refreshToken");
        this.configService.resetCurrentUser();
        this.router.navigate(['/login']);
    }
}
