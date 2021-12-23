import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DashboardObservable {

    private listeners = new Subject<any>();

    listen(): Observable<any> {
        return this.listeners.asObservable();
    }

    setDashboard(data: any) {
        this.listeners.next(data);
    }
}
