import { Range } from '../../component/system/date-range/model/model';
import { AbstractFilterModel } from './abstract-filter.model';

export class SendDocumentFilterModel implements AbstractFilterModel{

  organisation: string;
  documentStatus: string;
  documentType: string;
  receiverIdRaw: string;
  senderIdRaw: string;
  sentMessageId: string;
  documentId: string;
  documentDate: string;
  dateRange: Range;
  sortBy: string;

  constructor() {
    this.documentStatus = 'ALL';
    this.documentType = 'ALL';
    this.organisation = 'ALL';
    this.receiverIdRaw = null;
    this.sentMessageId = null;
    this.senderIdRaw = null;
    this.documentId = null;
    this.documentDate = null;
    this.dateRange = null;
    this.sortBy = 'orderBy_createTime_Desc';
  }
}
