import { DateRangePicker } from "../../../bs-component/components/daterange/date.range.picker";

export class IdentifierFilterProcessResult {

    organisation: string;
    identifierGroup: string;
    type: string;
    value: string;
    uniqueValueType: string;
    status: string;
    name: string;
    publishingStatus: string;
    dateRange: DateRangePicker;
    sortBy: string;

    constructor() {
        this.type = null;
        this.value = null;
        this.organisation = null;
        this.identifierGroup = null;
        this.uniqueValueType = null;
        this.name = null;
        this.status = 'ALL';
        this.publishingStatus = 'ALL';
        this.dateRange = null;
        this.sortBy = 'orderBy_Id_Desc';
    }
}
