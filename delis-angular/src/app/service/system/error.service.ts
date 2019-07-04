import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { RuntimeConfigService } from './runtime-config.service';
import { TokenService } from './token.service';
import { LoginModel } from '../../model/system/login.model';
import { ErrorModel } from '../../model/system/error.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor(
    private tokenService: TokenService,
    private router: Router,
    private configService: RuntimeConfigService) {
  }

  errorProcess(error: any): ErrorModel {
    let errorModel = new ErrorModel();
    switch (String(error['status'])) {
      case '401' : {
        let errorToken: string = JSON.stringify(error.error.error);
        if (new String(JSON.parse(errorToken)).valueOf() === new String('invalid_token').valueOf()) {
          this.tokenService.refreshTokenInit(localStorage.getItem('refreshToken')).subscribe(
            (data: {}) => {
              let loginModel: LoginModel = data['data'];
              this.tokenService.setToken(loginModel.accessToken);
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
        errorModel.status = String(error['status']);
        errorModel.message = error.error.fieldErrors[0].message;
      } break;
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
      } break;
      default : {
        errorModel.status = String(error['status']);
        errorModel.message = error.error.fieldErrors[0].message;
      }
    }
    return errorModel;
  }

  resetProcess() {
    this.tokenService.resetToken();
    localStorage.removeItem('refreshToken');
    this.configService.resetCurrentUser();
    this.router.navigate(['login']);
  }
}