import {DateRangeModel} from '../../../../models/date.range.model';

export class SendDocumentsFilterProcessResult {

    organisation: string;
    documentStatus: string;
    documentType: string;
    receiverIdRaw: string;
    senderIdRaw: string;
    sentMessageId: string;
    documentId: string;
    documentDate: string;
    dateRange: DateRangeModel;
    sortBy: string;

    constructor() {
        this.documentStatus = 'ALL';
        this.documentType = 'ALL';
        this.organisation = null;
        this.receiverIdRaw = null;
        this.sentMessageId = null;
        this.senderIdRaw = null;
        this.documentId = null;
        this.documentDate = null;
        this.dateRange = null;
        this.sortBy = 'orderBy_Id_Desc';
    }
}
