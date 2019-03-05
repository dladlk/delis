import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { DateRangeModel } from "../../../../models/date.range.model";

@Injectable()
export class DaterangeService {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    loadDate(dateRangeModel: DateRangeModel) {
        this._listeners.next(dateRangeModel);
    }
}
