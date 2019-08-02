import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { RuntimeConfigService } from './runtime-config.service';
import { TokenService } from './token.service';
import { ErrorModel } from '../../model/system/error.model';
import { LogoutService } from "./logout.service";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor(
      private router: Router,
      private tokenService: TokenService,
      private configService: RuntimeConfigService,
      private logoutService: LogoutService) {
  }

  errorProcess(error: any): ErrorModel {
    let errorModel = new ErrorModel();
    switch (String(error['status'])) {
      case '401' : {
        this.logoutService.logout();
        break;
      }
      case '403' : {
        errorModel.status = String(error['status']);
        errorModel.message = error.error.fieldErrors[0].message;
        break;
      }
      case '409' : {
        errorModel.status = String(error['status']);
        if (error.error instanceof ArrayBuffer) {
          let decodedString = String.fromCharCode.apply(null, new Uint8Array(error.error));
          let obj = JSON.parse(decodedString);
          errorModel.message = obj.fieldErrors[0].message;
          errorModel.details = obj.fieldErrors[0].details;
        } else {
          errorModel.message = error.error.fieldErrors[0].message;
          errorModel.details = error.error.fieldErrors[0].details;
        }
        break;
      }
      default : {
        errorModel.status = String(error['status']);
        errorModel.message = error.error.fieldErrors[0].message;
      }
    }
    return errorModel;
  }
}
