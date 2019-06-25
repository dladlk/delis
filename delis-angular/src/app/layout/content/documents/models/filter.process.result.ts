import { DateRangePicker } from "../../../bs-component/components/daterange/date.range.picker";

export class FilterProcessResult {

    status: string;
    lastError: string;
    ingoingFormat: string;
    organisation: string;
    receiver: string;
    documentType: string;
    senderName: string;
    dateReceived: DateRangePicker;
    sortBy: string;

    constructor() {
        this.status = 'ALL';
        this.lastError = 'ALL';
        this.ingoingFormat = 'ALL';
        this.organisation = 'ALL';
        this.receiver = null;
        this.documentType = 'ALL';
        this.senderName = null;
        this.dateReceived = null;
        this.sortBy = 'orderBy_Id_Desc';
    }
}
