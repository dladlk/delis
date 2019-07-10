import { MatSort } from "@angular/material";
import { Range } from '../../component/system/date-range/model/model';
import { TableStateModel } from "./table-state.model";

export class IdentifierFilterModel extends TableStateModel {

  organisation: string;
  identifierGroup: string;
  type: string;
  value: string;
  uniqueValueType: string;
  status: string;
  name: string;
  publishingStatus: string;
  dateRange: Range;

  constructor(sort: MatSort) {
    super(sort);
    this.type = null;
    this.value = null;
    this.organisation = 'ALL';
    this.identifierGroup = null;
    this.uniqueValueType = null;
    this.name = null;
    this.status = 'ALL';
    this.publishingStatus = 'ALL';
  }
}
