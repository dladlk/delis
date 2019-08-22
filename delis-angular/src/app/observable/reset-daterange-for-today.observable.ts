import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ResetDaterangeForTodayObservable {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    resetForToday() {
        this._listeners.next();
    }
}
