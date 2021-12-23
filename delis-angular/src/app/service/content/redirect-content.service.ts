import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class RedirectContentService {

    get redirectData(): any {
        return this.data;
    }

    private data: any;

    updateRedirectData(data: any) {
        this.data = data;
    }

    resetRedirectData() {
        this.data = undefined;
    }
}
