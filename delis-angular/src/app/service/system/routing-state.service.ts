import { Injectable } from "@angular/core";
import { NavigationEnd, Router } from "@angular/router";
import { filter } from "rxjs/operators";

import { DASHBOARD_PATH } from '../../app.constants';

@Injectable({
    providedIn: 'root'
})
export class RoutingStateService {

    private history = [];

    constructor(private router: Router) { }

    public loadRouting(): void {
        this.router.events
            .pipe(filter(event => event instanceof NavigationEnd))
            .subscribe(({urlAfterRedirects}: NavigationEnd) => {
                if (this.history.length >= 10) {
                    this.history = [];
                }
                if (this.history[this.history.length - 1]) {
                    if (urlAfterRedirects !== this.history[this.history.length - 1]) {
                        this.history = [...this.history, urlAfterRedirects];
                    }
                } else {
                    this.history = [...this.history, urlAfterRedirects];
                }
            });
    }

    public getPreviousUrl(): string {
        let previousUrl = this.history[this.history.length - 2] || '/' + DASHBOARD_PATH;
        if (this.history.length !== 0) {
            this.history.pop();
        }
        return previousUrl;
    }
}
