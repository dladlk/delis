import { Injectable, Inject, InjectionToken } from '@angular/core';
import { RangeUpdate } from '../model/model';
import { Subject } from 'rxjs';

export const DATE = new InjectionToken<Date>('date');

@Injectable({
  providedIn: 'root'
})
export class RangeStoreService {

  rangeUpdate$: Subject<RangeUpdate> = new Subject<RangeUpdate>();

  constructor(@Inject(DATE) private _fromDate: Date, @Inject(DATE) private _toDate: Date) { }

  get fromDate(): Date {
    return this._fromDate;
  }

  get toDate(): Date {
    return this._toDate;
  }

  updateRange(rangeUpdate: RangeUpdate) {
    this._fromDate = rangeUpdate.range.fromDate;
    this._toDate = rangeUpdate.range.toDate;
    this.rangeUpdate$.next(rangeUpdate);
  }
}
