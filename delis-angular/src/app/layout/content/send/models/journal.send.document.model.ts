import {SendDocumentsModel} from './send.documents.model';
import {OrganisationModel} from '../../organisation/models/organisation.model';

export class JournalSendDocumentModel {

    id: number;
    createTime: string;
    success: boolean;
    type: string;
    message: string;
    durationMs: number;
    organisation: OrganisationModel = new OrganisationModel();
    document: SendDocumentsModel = new SendDocumentsModel();
}
