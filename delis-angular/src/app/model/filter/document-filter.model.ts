import {Range} from '../../component/system/date-range/model/model';

export class DocumentFilterModel {

  documentStatus: string;
  lastError: string;
  ingoingDocumentFormat: string;
  organisation: string;
  receiverIdentifier: string;
  documentType: string;
  senderName: string;
  createTime: Range;
  sortBy: string;

  constructor() {
    this.documentStatus = 'ALL';
    this.lastError = 'ALL';
    this.ingoingDocumentFormat = 'ALL';
    this.organisation = 'ALL';
    this.receiverIdentifier = null;
    this.documentType = 'ALL';
    this.senderName = null;
    this.createTime = null;
    this.sortBy = 'orderBy_createTime_Desc';
  }
}
