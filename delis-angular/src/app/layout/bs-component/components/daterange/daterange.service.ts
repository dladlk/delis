import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { DateRangePicker } from "./date.range.picker";

@Injectable()
export class DaterangeService {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    loadDate(dateRangeModel: DateRangePicker) {
        this._listeners.next(dateRangeModel);
    }
}
