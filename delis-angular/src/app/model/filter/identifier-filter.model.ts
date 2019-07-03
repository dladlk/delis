import { Range } from '../../component/system/date-range/model/model';

export class IdentifierFilterModel {

  organisation: string;
  identifierGroup: string;
  type: string;
  value: string;
  uniqueValueType: string;
  status: string;
  name: string;
  publishingStatus: string;
  dateRange: Range;
  sortBy: string;

  constructor() {
    this.type = null;
    this.value = null;
    this.organisation = 'ALL';
    this.identifierGroup = null;
    this.uniqueValueType = null;
    this.name = null;
    this.status = 'ALL';
    this.publishingStatus = 'ALL';
    this.dateRange = null;
    this.sortBy = 'orderBy_createTime_Desc';
  }
}
