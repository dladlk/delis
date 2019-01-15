import { DateRangeModel } from "../../../../models/date.range.model";

export class JournalOrganisationFilterProcessResult {

    organisation: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;

    countClickOrganisation: number;
    countClickCreateTime: number;
    countClickMessage: number;
    countClickDurationMs: number;

    constructor() {

        this.organisation = null;
        this.message = null;
        this.durationMs = null;
        this.dateRange = null;
        this.countClickOrganisation = 0;
        this.countClickCreateTime = 0;
        this.countClickMessage = 0;
        this.countClickDurationMs = 0;
    }
}
