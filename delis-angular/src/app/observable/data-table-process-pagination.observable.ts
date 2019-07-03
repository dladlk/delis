import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {DateRangePickerModel} from '../model/system/date-range-picker.model';

@Injectable({
  providedIn: 'root'
})
export class DataTableProcessPaginationObservable {

  private listeners = new Subject<any>();

  listen(): Observable<any> {
    return this.listeners.asObservable();
  }

  loadDate(dateRangeModel: DateRangePickerModel) {
    this.listeners.next(dateRangeModel);
  }
}
