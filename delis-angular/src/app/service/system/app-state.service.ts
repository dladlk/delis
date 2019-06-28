import { Injectable } from '@angular/core';
import { IAppPageState } from '../../model/state/app-page-state';

@Injectable({
  providedIn: 'root'
})
export class AppStateService {

  constructor() {}

  getState(key: any) {
    const filter = localStorage.getItem(key);
    if (filter === null) {
      return null;
    } else {
      return JSON.parse(filter);
    }
  }

  addState(state: IAppPageState) {
    localStorage.setItem(state.type, JSON.stringify(state.details));
  }

  updateState(state: IAppPageState) {
    localStorage.setItem(state.type, JSON.stringify(state.details));
  }
}
