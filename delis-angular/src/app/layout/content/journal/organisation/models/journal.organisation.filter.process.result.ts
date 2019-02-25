import { DateRangeModel } from "../../../../../models/date.range.model";

export class JournalOrganisationFilterProcessResult {

    organisation: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;
    sortBy: string;

    constructor() {

        this.organisation = null;
        this.message = null;
        this.durationMs = null;
        this.dateRange = null;
        this.sortBy = 'orderBy_Id_Asc';
    }
}
