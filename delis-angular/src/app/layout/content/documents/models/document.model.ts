import { OrganisationModel } from "../../organisation/models/organisation.model";
import { IdentifierModel } from "../../identifier/models/identifier.model";

export class DocumentModel {

    id: number;
    createTime: string;
    updateTime: string;
    name: string;
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
    lastError: string;

    organisation: OrganisationModel = new OrganisationModel();
    receiverIdentifier: IdentifierModel = new IdentifierModel();
}
