import { Injectable } from '@angular/core';
import { IdentifierFilterModel } from '../../model/filter/identifier-filter.model';
import { StateService } from './state-service';

@Injectable({
  providedIn: 'root'
})
export class IdentifierStateService implements StateService<IdentifierFilterModel> {

  filter: IdentifierFilterModel;

  getFilter(): IdentifierFilterModel {
    return this.filter;
  }

  setFilter(filter: IdentifierFilterModel) {
    this.filter = filter;
  }
}
