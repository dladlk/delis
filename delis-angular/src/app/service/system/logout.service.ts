import { Injectable } from '@angular/core';

import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime-config.service";
import { CheckExpirationService } from "./check-expiration.service";

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(
      private tokenService: TokenService,
      private checkExpirationService: CheckExpirationService,
      private configService: RuntimeConfigService) { }

  logout() {
      this.tokenService.resetToken();
      this.configService.resetCurrentUser();
      this.checkExpirationService.resetExpiration();
      window.location.reload();
  }
}
