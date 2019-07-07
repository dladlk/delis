import { DashboardDocumentData } from "./dashboard-document.data";

export class DashboardDocumentAdminData implements DashboardDocumentData {

    organisation: string;
    receiverIdentifierNames: string[];
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
