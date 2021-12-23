import { Injectable } from '@angular/core';
import { SendDocumentFilterModel } from '../../model/filter/send-document-filter.model';
import { StateService } from './state-service';

@Injectable({
  providedIn: 'root'
})
export class SendDocumentStateService implements StateService<SendDocumentFilterModel> {

  filter: SendDocumentFilterModel;

  getFilter(): SendDocumentFilterModel {
    return this.filter;
  }

  setFilter(filter: SendDocumentFilterModel) {
    this.filter = filter;
  }
}
