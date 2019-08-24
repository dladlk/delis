import { MatSort } from "@angular/material";
import { TableStateModel } from "./table-state.model";

export class DocumentFilterModel extends TableStateModel {

  documentStatus: string;
  lastError: string;
  ingoingDocumentFormat: string;
  organisation: string;
  receiverIdentifier: string;
  documentType: string;
  senderName: string;

  constructor(sort: MatSort) {
    super(sort);
    this.documentStatus = 'ALL';
    this.lastError = 'ALL';
    this.ingoingDocumentFormat = 'ALL';
    this.organisation = 'ALL';
    this.receiverIdentifier = null;
    this.documentType = 'ALL';
    this.senderName = null;
  }
}
