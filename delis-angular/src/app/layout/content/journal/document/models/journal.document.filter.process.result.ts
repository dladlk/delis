import {DateRangeModel} from "../../../../../models/date.range.model";

export class JournalDocumentFilterProcessResult {

    organisation: string;
    document: string;
    type: string;
    success: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;
    sortBy: string;

    constructor() {
        this.type = 'ALL';
        this.success = 'ALL';
        this.organisation = null;
        this.document = null;
        this.message = null;
        this.durationMs = null;
        this.dateRange = null;
        this.sortBy = 'orderBy_Id_Desc';
    }
}
