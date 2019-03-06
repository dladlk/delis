import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";

@Injectable()
export class DaterangeShowService {

    private _listeners = new Subject<any>();

    public listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    public hide(show: boolean) {
        this._listeners.next(show);
    }
}
