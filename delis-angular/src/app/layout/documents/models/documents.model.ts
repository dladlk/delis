import { DateRangeModel } from '../../../models/date.range.model';

export class DocumentsModel {

    id: number;
    organisation: string; // Region Nord, Region Syd, Region Midt
    receiver: string; // EAN 9920191209017, EAN 9920191209024, EAN 9920191209093
    status: string;
    lastError: string;
    documentType: string;
    ingoingFormat: string;
    received: string; // date
    issued: string; // date
    senderName: string;
    receiverName: string;

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.organisation = model.organisation;
            this.receiver = model.receiver;
            this.status = model.status;
            this.lastError = model.lastError;
            this.documentType = model.documentType;
            this.ingoingFormat = model.ingoingFormat;
            this.received = model.received;
            this.issued = model.issued;
            this.senderName = model.senderName;
            this.receiverName = model.receiverName;
        }
    }
}

export class FilterProcessResult {

    status: string;
    lastError: string;
    ingoingFormat: string;
    organisation: string;
    receiver: string;
    documentType: string;
    senderName: string;
    receiverName: string;
    dateReceived: DateRangeModel;
    dateIssued: DateRangeModel;

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
        this.receiverName = null;
        this.dateReceived = new DateRangeModel(new Date(), new Date());
        this.dateIssued = new DateRangeModel(new Date(), new Date());
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

export enum Status {

    LOAD_OK = 'LOAD_OK',
    VALIDATE_OK = 'VALIDATE_OK',
    VALIDATE_ERROR = 'VALIDATE_ERROR',
    EXPORT_OK = 'EXPORT_OK',
    DELIVER_OK = 'DELIVER_OK'
}

export enum LastError {

    BIS3_XSD = 'BIS3_XSD',
    BIS3_SCH = 'BIS3_SCH',
    OIOUBL_XSD = 'OIOUBL_XSD',
    OIOUBL_SCH = 'OIOUBL_SCH',
    CII_XSD = 'CII_XSD',
    CII_SCH = 'CII_SCH'
}

export enum IngoingFormat {

    CII = 'CII',
    BIS3_INVOICE = 'BIS3_INVOICE',
    BIS3_CREDITNOTE = 'BIS3_CREDITNOTE',
    OIOUBL_INVOICE = 'OIOUBL_INVOICE',
    OIOUBL_CREDITNOTE = 'OIOUBL_CREDITNOTE'
}

export enum DocumentType {

    UNSUPPORTED = 'UNSUPPORTED',
    INVOICE = 'INVOICE',
    CREDITNOTE = 'CREDITNOTE'
}
