import { OrganisationModel } from "../../organisation/models/organisation.model";
import { IdentifierModel } from "../../identifier/models/identifier.model";

export class DocumentModel {

    id: number;
    createTime: string;
    updateTime: string;
    ingoingRelativePath: string;
    documentStatus: string;
    documentType: string;
    receiverIdRaw: string;
    receiverName: string;
    receiverCountry: string;
    senderIdRaw: string;
    senderName: string;
    senderCountry: string;
    ingoingDocumentFormat: string;
    documentId: string;
    documentDate: string;
    messageId: string;
    outgoingRelativePath: string;
    lastError: string;

    organisation: OrganisationModel;
    receiverIdentifier: IdentifierModel;

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.createTime = model.createTime;
            this.updateTime = model.updateTime;
            this.ingoingRelativePath = model.ingoingRelativePath;
            this.documentStatus = model.documentStatus;
            this.documentType = model.documentType;
            this.receiverIdRaw = model.receiverIdRaw;
            this.receiverName = model.receiverName;
            this.receiverCountry = model.receiverCountry;
            this.senderIdRaw = model.senderIdRaw;
            this.senderName = model.senderName;
            this.senderCountry = model.senderCountry;
            this.ingoingDocumentFormat = model.ingoingDocumentFormat;
            this.documentId = model.documentId;
            this.documentDate = model.documentDate;
            this.messageId = model.messageId;
            this.outgoingRelativePath = model.outgoingRelativePath;
            this.lastError = model.lastError;
            this.organisation = model.organisation;
            this.receiverIdentifier = model.receiverIdentifier;
        }
    }
}
