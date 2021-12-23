import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DelisEntityDetailsObservable {

    private listeners = new Subject<any>();

    listen(): Observable<any> {
        return this.listeners.asObservable();
    }

    loadCurrentId(id: any) {
        this.listeners.next(id);
    }
}
