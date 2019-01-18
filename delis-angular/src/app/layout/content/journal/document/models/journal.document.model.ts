import { OrganisationModel } from "../../../organisation/models/organisation.model";
import { DocumentModel } from "../../../documents/models/document.model";

export class JournalDocumentModel {

    id: number;
    createTime: string;
    updateTime: string;
    organisation: OrganisationModel;
    document: DocumentModel;
    success: boolean;
    type: string;
    message: string;
    durationMs: number;

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.createTime = model.createTime;
            this.updateTime = model.updateTime;
            this.organisation = model.organisation;
            this.document = model.document;
            this.success = model.success;
            this.type = model.type;
            this.message = model.message;
            this.durationMs = model.durationMs;
        }
    }
}