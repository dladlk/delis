import { DateRangePickerModel } from '../system/date-range-picker.model';

export class JournalIdentifierFilterModel {

  organisation: string;
  identifier: string;
  message: string;
  durationMs: number;
  dateRange: DateRangePickerModel;
  sortBy: string;

  constructor() {

    this.organisation = null;
    this.identifier = null;
    this.message = null;
    this.durationMs = null;
    this.dateRange = null;
    this.sortBy = 'orderBy_Id_Desc';
  }
}
