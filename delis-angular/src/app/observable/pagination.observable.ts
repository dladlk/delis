import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { PaginationModel } from '../model/system/pagination.model';

@Injectable({
  providedIn: 'root'
})
export class PaginationObservable {

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

  refresh(pagination: PaginationModel) {
    this._listeners.next(pagination);
  }

  clearFilter() {
    this._listeners.next(null);
  }
}
