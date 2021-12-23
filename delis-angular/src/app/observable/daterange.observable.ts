import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Range } from '../component/system/date-range/model/model';

@Injectable({
  providedIn: 'root'
})
export class DaterangeObservable {

  private listeners = new Subject<any>();

  listen(): Observable<any> {
    return this.listeners.asObservable();
  }

  loadDate(range: Range) {
    this.listeners.next(range);
  }
}
