import { Injectable } from '@angular/core';
import { NgxDrpOptions } from '../model/model';

@Injectable({
  providedIn: 'root'
})
export class ConfigStoreService {

  private ngxDrpOptionsLocal: NgxDrpOptions;
  private defaultOptions = {
    excludeWeekends: false,
    animation: true,
    locale: 'da-DK',
    fromMinMax: { fromDate: null, toDate: null },
    toMinMax: { fromDate: null, toDate: null }
  };

  constructor() {}

  get ngxDrpOptions(): NgxDrpOptions {
    return this.ngxDrpOptionsLocal;
  }

  set ngxDrpOptions(options: NgxDrpOptions) {
    this.ngxDrpOptionsLocal = { ...this.defaultOptions, ...options };
  }
}
