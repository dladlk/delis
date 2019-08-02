import { Injectable } from '@angular/core';
import { DocumentFilterModel } from "../../model/filter/document-filter.model";
import { StateService } from "./state-service";

@Injectable({
  providedIn: 'root'
})
export class DocumentStateService implements StateService<DocumentFilterModel>{

  filter: DocumentFilterModel;

  getFilter(): DocumentFilterModel {
    return this.filter;
  }

  setFilter(filter: DocumentFilterModel) {
    this.filter = filter;
  }
}
