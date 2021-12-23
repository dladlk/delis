import { Injectable, Inject, InjectionToken } from '@angular/core';
import { RangeUpdate } from '../model/model';
import { Subject } from 'rxjs';

export const DATE = new InjectionToken<Date>('date');

@Injectable({
  providedIn: 'root'
})
export class RangeStoreService {

  rangeUpdate$: Subject<RangeUpdate> = new Subject<RangeUpdate>();

  constructor(@Inject(DATE) private from: Date, @Inject(DATE) private to: Date) { }

  get fromDate(): Date {
    return this.from;
  }

  get toDate(): Date {
    return this.to;
  }

  updateRange(rangeUpdate: RangeUpdate) {
    this.from = rangeUpdate.range.fromDate;
    this.to = rangeUpdate.range.toDate;
    this.rangeUpdate$.next(rangeUpdate);
  }
}
