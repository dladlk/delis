import { DocumentModel } from "./document.model";

export class DocumentBytesModel {

    id: number;
    createTime: string;
    type: string;
    size: number;
    format: string;
    document: DocumentModel = new DocumentModel();
}
