import {DateRangeModel} from "../../../../../models/date.range.model";

export class JournalIdentifierFilterProcessResultModel {

    organisation: string;
    identifier: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;
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
