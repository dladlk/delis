import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { PaginationModel } from "./pagination.model";

@Injectable()
export class PaginationService {

    private _listeners = new Subject<any>();

    listen(): Observable<any> {
        return this._listeners.asObservable();
    }

    loadPage(pagination: PaginationModel) {
        this._listeners.next(pagination);
    }

    loadPageSize(pagination: PaginationModel) {
        this._listeners.next(pagination);
    }

    clearFilter() {
        this._listeners.next();
    }
}
