import { Injectable } from '@angular/core';

import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime-config.service";

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private tokenService: TokenService, private configService: RuntimeConfigService) { }

  logout() {
      this.tokenService.resetToken();
      this.configService.resetCurrentUser();
      window.location.reload();
  }
}
