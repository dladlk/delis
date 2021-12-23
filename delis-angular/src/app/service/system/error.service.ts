import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { RuntimeConfigService } from './runtime-config.service';
import { TokenService } from './token.service';
import { ErrorModel } from '../../model/system/error.model';
import { LogoutService } from './logout.service';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor(
    private router: Router,
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private logoutService: LogoutService) { }

  errorProcess(error: any): ErrorModel {
    const errorModel = new ErrorModel();

    try {

      const getErrorMessages = (errorContainer: any) => {
        if (errorContainer && errorContainer.error) {
          if (errorContainer.error.hasOwnProperty('fieldErrors')) {
            return errorContainer.error.fieldErrors;
          }
        }
        return null;
      };

      console.log('ErrorService.errorProcess is invoked with error status ' + error.status + ':');
      console.log(error);

      errorModel.status = String(error.status);

      switch (String(error.status)) {
        case '401' : {
          this.logoutService.logout();
          break;
        }
        case '403' : {
          const errorMessages = getErrorMessages(error);
          if (errorMessages && errorMessages.length) {
            errorModel.message = errorMessages[0].message;
          }
          break;
        }
        case '409' : {
          if (error.error instanceof ArrayBuffer) {
            const decodedString = this.getDecodedString(error.error);
            errorModel.message = decodedString.fieldErrors[0].message;
            errorModel.details = decodedString.fieldErrors[0].details;
          } else {
            const errorMessages = getErrorMessages(error);
            if (errorMessages && errorMessages.length) {
              errorModel.message = errorMessages[0].message;
              errorModel.details = errorMessages[0].details;
            }
          }
          break;
        }
        case '500' : {
          if (error.hasOwnProperty('error')) {
            if (error.error instanceof ArrayBuffer) {
              const decodedString = this.getDecodedString(error.error);
              errorModel.message = decodedString.message;
            } else {
              errorModel.message = error.error.message;
            }
          } else {
            errorModel.message = error.message;
          }
          break;
        }
        default : {
          const errorMessages = getErrorMessages(error);
          if (errorMessages && errorMessages.length) {
            errorModel.message = errorMessages.message;
          }
        }
      }

      console.log('Result of conversion to error model:');
      console.log(errorModel);

    } catch (unexpectedError) {
      console.log('Unexpected error occurred during error processing of:');
      console.log(error);

      console.log('Occurred error: ');
      console.log(unexpectedError);
    }

    return errorModel;
  }

  getDecodedString(error: any) {
    const decodedString = String.fromCharCode.apply(null, new Uint8Array(error));
    return JSON.parse(decodedString);
  }
}
