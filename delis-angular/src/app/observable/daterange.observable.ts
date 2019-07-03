import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Range } from '../component/system/date-range/model/model';

@Injectable({
  providedIn: 'root'
})
export class DaterangeObservable {

  private _listeners = new Subject<any>();

  listen(): Observable<any> {
    return this._listeners.asObservable();
  }

  loadDate(range: Range) {
    this._listeners.next(range);
  }
}
