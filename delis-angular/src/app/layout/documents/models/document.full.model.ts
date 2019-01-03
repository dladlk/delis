export class DocumentFullModel {

    id: number;
    createTime: string;
    ingoingRelativePath: string;
    documentStatus: string;
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

    constructor(model ?: any) {
        if (model) {
            this.id = model.id;
            this.createTime = model.createTime;
            this.ingoingRelativePath = model.ingoingRelativePath;
            this.documentStatus = model.documentStatus;
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
        }
    }
}