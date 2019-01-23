import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ErrorModel } from "../models/error.model";

@Injectable()
export class ListenErrorService {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    loadError(error: ErrorModel) {
        this._listeners.next(error);
    }
}
