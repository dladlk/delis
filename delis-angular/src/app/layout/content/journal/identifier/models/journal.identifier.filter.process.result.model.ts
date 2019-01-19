import {DateRangeModel} from "../../../../../models/date.range.model";

export class JournalIdentifierFilterProcessResultModel {

    organisation: string;
    identifier: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;

    countClickOrganisation: number;
    countClickIdentifier: number;
    countClickCreateTime: number;
    countClickMessage: number;
    countClickDurationMs: number;

    constructor() {

        this.organisation = null;
        this.identifier = null;
        this.message = null;
        this.durationMs = null;
        this.dateRange = null;
        this.countClickOrganisation = 0;
        this.countClickIdentifier = 0;
        this.countClickCreateTime = 0;
        this.countClickMessage = 0;
        this.countClickDurationMs = 0;
    }
}
