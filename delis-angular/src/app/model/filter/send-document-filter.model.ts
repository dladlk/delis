import { MatSort } from "@angular/material";
import { Range } from '../../component/system/date-range/model/model';
import { TableStateModel } from "./table-state.model";

export class SendDocumentFilterModel extends TableStateModel {

  organisation: string;
  documentStatus: string;
  documentType: string;
  receiverIdRaw: string;
  senderIdRaw: string;
  sentMessageId: string;
  documentId: string;
  documentDate: string;
  dateRange: Range;

  constructor(sort: MatSort) {
    super(sort);
    this.documentStatus = 'ALL';
    this.documentType = 'ALL';
    this.organisation = 'ALL';
    this.receiverIdRaw = null;
    this.sentMessageId = null;
    this.senderIdRaw = null;
    this.documentId = null;
    this.documentDate = null;
    this.dateRange = null;
  }
}
