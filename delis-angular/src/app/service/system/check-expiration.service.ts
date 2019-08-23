import { Injectable } from "@angular/core";

@Injectable({
    providedIn: "root"
})
export class CheckExpirationService {

    private EXPIRATION_DATE = 'expirationDate';

    setExpiration(expiration: Date): void {
        localStorage.setItem(this.EXPIRATION_DATE, JSON.stringify(expiration));
    }

    isExpired(): boolean {
        let expiration = localStorage.getItem(this.EXPIRATION_DATE);
        if (expiration === null) {
            return true;
        }
        let now = new Date();
        let expired = new Date(JSON.parse(expiration));
        return expired.getTime() < now.getTime();
    }

    resetExpiration(): void {
        localStorage.removeItem(this.EXPIRATION_DATE);
    }
}
