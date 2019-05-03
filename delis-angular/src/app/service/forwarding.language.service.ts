import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";

@Injectable()
export class ForwardingLanguageService {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    forwardLanguage(lang: string) {
        this._listeners.next(lang);
    }
}
