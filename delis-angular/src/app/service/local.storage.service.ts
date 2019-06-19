import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable()
export class LocalStorageService {

    protected subjects: {[key: string]: BehaviorSubject<any>} = {};

    public select(key: string, defaultValue: any = null): Observable<any> {
        if (this.subjects.hasOwnProperty(key)) {
            return this.subjects[key];
        }
        if (!window.localStorage.getItem(key) && defaultValue) {
            window.localStorage.setItem(key, JSON.stringify(defaultValue));
        }
        const value = window.localStorage.getItem(key)
            ? JSON.parse(window.localStorage.getItem(key))
            : defaultValue;

        return this.subjects[key] = new BehaviorSubject(value);
    }

    set(key: string, value: any): void {
        window.localStorage.setItem(key, JSON.stringify(value));
        if (this.subjects.hasOwnProperty(key)) {
            this.subjects[key].next(value);
        }
    }
}
