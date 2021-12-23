import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ResetDaterangeForTodayObservable {

    private listeners = new Subject<any>();

    listen(): Observable<any> {
        return this.listeners.asObservable();
    }

    resetForToday() {
        this.listeners.next();
    }
}
