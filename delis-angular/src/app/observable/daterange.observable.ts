import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { DateRangePickerModel } from '../model/system/date-range-picker.model';

@Injectable({
  providedIn: 'root'
})
export class DaterangeObservable {

  private _listeners = new Subject<any>();

  listen(): Observable<any> {
    return this._listeners.asObservable();
  }

  loadDate(dateRangeModel: DateRangePickerModel) {
    this._listeners.next(dateRangeModel);
  }
}
