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

    try {

    const getErrorMessages = (error: any) => {
      if (error && error.error) {
        if (error.error.hasOwnProperty('fieldErrors')) {
          return error.error.fieldErrors;
        }
      }
      return null;
    };

    console.log('ErrorService.errorProcess is invoked with error status '+ error['status'] + ':');
    console.log(error);

    switch (String(error['status'])) {
      case '401' : {
        this.logoutService.logout();
        break;
      }
      case '403' : {
        errorModel.status = String(error['status']);
        let errorMessages = getErrorMessages(error);
        if (errorMessages && errorMessages.length) {
          errorModel.message = errorMessages[0].message;
        }
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
          let errorMessages = getErrorMessages(error);
          if (errorMessages && errorMessages.length) {
            errorModel.message = errorMessages.message;
            errorModel.details = errorMessages.details;
          }
        }
        break;
      }
      default : {
        errorModel.status = String(error['status']);
        let errorMessages = getErrorMessages(error);
        if (errorMessages && errorMessages.length) {
          errorModel.message = errorMessages.message;
        }
      }
    }

    console.log('Result of conversion to error model:');
    console.log(errorModel);

  } catch (unexpectedError) {
    console.log('Unexpected error occured during error processing of:');
    console.log(error);

    console.log('Occured error: ');
    console.log(unexpectedError);
  }

    return errorModel;
  }
}
