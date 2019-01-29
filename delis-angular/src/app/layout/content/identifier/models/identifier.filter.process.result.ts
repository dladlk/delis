import { DateRangeModel } from "../../../../models/date.range.model";

export class IdentifierFilterProcessResult {

    organisation: string;
    identifierGroup: string;
    type: string;
    value: string;
    uniqueValueType: string;
    status: string;
    name: string;
    publishingStatus: string;
    dateRange: DateRangeModel;

    countClickOrganisation: number;
    countClickIdentifierGroup: number;
    countClickCreateTime: number;
    countClickType: number;
    countClickValue: number;
    countClickUniqueValueType: number;
    countClickStatus: number;
    countClickPublishingStatus: number;
    countClickName: number;

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
        this.countClickOrganisation = 0;
        this.countClickIdentifierGroup = 0;
        this.countClickCreateTime = 0;
        this.countClickType = 0;
        this.countClickValue = 0;
        this.countClickUniqueValueType = 0;
        this.countClickStatus = 0;
        this.countClickPublishingStatus = 0;
        this.countClickName = 0;
    }
}