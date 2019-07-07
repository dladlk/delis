import { DashboardDocumentData } from "./dashboard-document.data";

export class DashboardDocumentUserData implements DashboardDocumentData {

    receiverIdentifierName: string;
    countDocuments: number;
    documentStatuses: string[];
    documentTypes: string[];
    documentsId: string[];
    ingoingDocumentFormats: string[];
    names: string[];
    receiverCountries: string[];
    receiverIdRaws: string[];
    receiverNames: string[];
    senderCountries: string[];
    senderIdRaws: string[];
    senderNames: string[];
}
