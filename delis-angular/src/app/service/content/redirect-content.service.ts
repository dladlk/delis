import { Injectable } from "@angular/core";

@Injectable({
    providedIn: "root"
})
export class RedirectContentService {

    get redirectData(): any {
        return this._redirectData;
    }

    updateRedirectData(data: any) {
        this._redirectData = data;
    }

    resetRedirectData() {
        this._redirectData = undefined;
    }

    private _redirectData: any;
}
