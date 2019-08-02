import { Injectable } from '@angular/core';
import { Router } from "@angular/router";

import { TokenService } from "./token.service";
import { RuntimeConfigService } from "./runtime-config.service";
import { HttpRestService } from "./http-rest.service";

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private router: Router, private tokenService: TokenService, private configService: RuntimeConfigService, private http: HttpRestService) { }

  logout() {
      this.tokenService.resetToken();
      this.configService.resetCurrentUser();
      this.router.navigate(['/login']);
  }
}
