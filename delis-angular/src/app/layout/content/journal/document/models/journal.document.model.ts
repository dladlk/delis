import { OrganisationModel } from "../../../organisation/models/organisation.model";
import { DocumentModel } from "../../../documents/models/document.model";

export class JournalDocumentModel {

    id: number;
    createTime: string;
    organisation: OrganisationModel = new OrganisationModel();
    document: DocumentModel = new DocumentModel();
    success: boolean;
    type: string;
    message: string;
    durationMs: number;
}
