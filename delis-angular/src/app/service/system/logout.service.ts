import { Injectable } from '@angular/core';

import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime-config.service";
import { CheckExpirationService } from "./check-expiration.service";
import { ChartDocumentService } from "../content/chart-document.service";

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(
      private tokenService: TokenService,
      private checkExpirationService: CheckExpirationService,
      private configService: RuntimeConfigService,
      private chartDocumentService: ChartDocumentService) { }

  logout() {
      this.tokenService.resetToken();
      this.configService.resetCurrentUser();
      this.checkExpirationService.resetExpiration();
      this.chartDocumentService.resetRange();
      window.location.reload();
  }
}
