import { MatSort } from "@angular/material";
import { Range } from '../../component/system/date-range/model/model';
import { TableStateModel } from "./table-state.model";

export class DocumentFilterModel extends TableStateModel {

  documentStatus: string;
  lastError: string;
  ingoingDocumentFormat: string;
  organisation: string;
  receiverIdentifier: string;
  documentType: string;
  senderName: string;
  createTime: Range;

  constructor(sort: MatSort) {
    super(sort);
    this.documentStatus = 'ALL';
    this.lastError = 'ALL';
    this.ingoingDocumentFormat = 'ALL';
    this.organisation = 'ALL';
    this.receiverIdentifier = null;
    this.documentType = 'ALL';
    this.senderName = null;
    this.createTime = null;
  }
}
