import { DocumentModel } from "./document.model";

export class DocumentBytesModel {

    id: number;
    createTime: string;
    type: string;
    size: number;
    document: DocumentModel = new DocumentModel();
}
