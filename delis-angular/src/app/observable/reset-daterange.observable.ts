import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ResetDaterangeObservable {

    private listeners = new Subject<any>();

    listen(): Observable<any> {
        return this.listeners.asObservable();
    }

    reset() {
        this.listeners.next();
    }
}
