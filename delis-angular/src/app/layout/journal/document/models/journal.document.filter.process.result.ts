import {DateRangeModel} from "../../../../models/date.range.model";

export class JournalDocumentFilterProcessResult {

    organisation: string;
    document: string;
    type: string;
    success: string;
    message: string;
    durationMs: number;
    dateRange: DateRangeModel;

    countClickOrganisation: number;
    countClickDocument: number;
    countClickCreateTime: number;
    countClickDocumentProcessStepType: number;
    countClickSuccess: number;
    countClickMessage: number;
    countClickDurationMs: number;

    constructor() {
        this.type = 'ALL';
        this.success = 'ALL';
        this.organisation = null;
        this.document = null;
        this.message = null;
        this.durationMs = null;
        this.dateRange = null;
        this.countClickOrganisation = 0;
        this.countClickDocument = 0;
        this.countClickCreateTime = 0;
        this.countClickDocumentProcessStepType = 0;
        this.countClickSuccess = 0;
        this.countClickMessage = 0;
        this.countClickDurationMs = 0;
    }
}
