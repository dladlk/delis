import { DateRangeModel } from '../../../../models/date.range.model';

export class FilterProcessResult {

    status: string;
    lastError: string;
    ingoingFormat: string;
    organisation: string;
    receiver: string;
    documentType: string;
    senderName: string;
    dateReceived: DateRangeModel;

    countClickOrganisation: number;
    countClickReceiver: number;
    countClickStatus: number;
    countClickLastError: number;
    countClickDocumentType: number;
    countClickIngoingFormat: number;
    countClickReceived: number;
    countClickIssued: number;
    countClickSenderName: number;
    countClickReceiverName: number;

    constructor() {
        this.status = 'ALL';
        this.lastError = 'ALL';
        this.ingoingFormat = 'ALL';
        this.organisation = null;
        this.receiver = null;
        this.documentType = 'ALL';
        this.senderName = null;
        this.dateReceived = null;
        this.countClickOrganisation = 0;
        this.countClickReceiver = 0;
        this.countClickStatus = 0;
        this.countClickLastError = 0;
        this.countClickDocumentType = 0;
        this.countClickIngoingFormat = 0;
        this.countClickReceived = 0;
        this.countClickIssued = 0;
        this.countClickSenderName = 0;
        this.countClickReceiverName = 0
    }
}
