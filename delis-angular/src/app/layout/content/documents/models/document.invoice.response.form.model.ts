import {InvoiceResponseGenerationModel} from './invoice.response.generation.model';

export class DocumentInvoiceResponseFormModel {

    documentId: number;
    usecase: string;
    generateWithoutSending: boolean;
    validate: boolean;
    messageLevelResponse: boolean;
    documentFormatName: string;
    data: InvoiceResponseGenerationModel;

    constructor() {
        this.generateWithoutSending = true;
        this.validate = true;
        this.documentFormatName = 'InvoiceResponse';
    }
}
