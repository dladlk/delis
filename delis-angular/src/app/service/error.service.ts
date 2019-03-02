import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { LocaleService } from "./locale.service";
import { ListenErrorService } from "./listen.error.service";
import { ErrorModel } from "../models/error.model";
import { LogoutService } from "../logout/logout.service";

@Injectable()
export class ErrorService {

    constructor(
        private http: HttpRestService,
        private localeService: LocaleService,
        private listenErrorService: ListenErrorService, private logout: LogoutService) {
    }

    errorProcess(error: any) {

        switch (String(error["status"])) {
            case "401" : {
                this.logout.logout();
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
