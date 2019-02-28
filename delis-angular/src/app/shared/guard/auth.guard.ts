import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { Router } from '@angular/router';
import { TokenService } from "../../service/token.service";

@Injectable()
export class AuthGuard implements CanActivate {

    constructor(private router: Router, private tokenService: TokenService) {}

    canActivate() {
        if (this.tokenService.isAuthenticated()) {
            return true;
        }
        this.router.navigate(['/login']);
        return false;
    }
}
