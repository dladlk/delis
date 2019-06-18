import {SendDocumentsModel} from './send.documents.model';

export class SendDocumentsBytesModel {

    id: number;
    createTime: string;
    type: string;
    size: number;
    document: SendDocumentsModel = new SendDocumentsModel();
}
