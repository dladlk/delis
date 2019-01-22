import { Injectable } from "@angular/core";
import * as data from '../documents.json';
import * as dataEnum from '../enum.json';
import { FilterProcessResult } from "../models/filter.process.result";
import { DocumentModel } from "../models/document.model";
import {EnumFieldModel} from "../models/enum.field.model";

@Injectable()
export class DocumentsTestGuiStaticService {

    constructor() {}

    filterProcess(parameters: { filter: FilterProcessResult }) : DocumentModel[] {
        let filter = parameters.filter;

        let documents = Object.assign([], data.docs);

        if (filter.status !== 'ALL') {
            documents = documents.filter(el => el.documentStatus === filter.status);
        }

        if (filter.lastError !== 'ALL') {
            documents = documents.filter(el => el.lastError === filter.lastError);
        }

        if (filter.documentType !== 'ALL') {
            documents = documents.filter(el => el.documentType === filter.documentType);
        }

        if (filter.ingoingFormat !== 'ALL') {
            documents = documents.filter(el => el.ingoingDocumentFormat === filter.ingoingFormat);
        }

        if (filter.countClickOrganisation === 1) {
            documents.sort((one, two) => (one.organisation < two.organisation ? -1 : 1));
        } else if (filter.countClickOrganisation === 2) {
            documents.sort((one, two) => (one.organisation > two.organisation ? -1 : 1));
        }

        if (filter.countClickReceiver === 1) {
            documents.sort((one, two) => (one.receiver < two.receiver ? -1 : 1));
        } else if (filter.countClickReceiver === 2) {
            documents.sort((one, two) => (one.receiver > two.receiver ? -1 : 1));
        }

        if (filter.countClickStatus === 1) {
            documents.sort((one, two) => (one.status < two.status ? -1 : 1));
        } else if (filter.countClickStatus === 2) {
            documents.sort((one, two) => (one.status > two.status ? -1 : 1));
        }

        if (filter.countClickLastError === 1) {
            documents.sort((one, two) => (one.lastError < two.lastError ? -1 : 1));
        } else if (filter.countClickLastError === 2) {
            documents.sort((one, two) => (one.lastError > two.lastError ? -1 : 1));
        }

        if (filter.countClickDocumentType === 1) {
            documents.sort((one, two) => (one.documentType < two.documentType ? -1 : 1));
        } else if (filter.countClickDocumentType === 2) {
            documents.sort((one, two) => (one.documentType > two.documentType ? -1 : 1));
        }

        if (filter.countClickIngoingFormat === 1) {
            documents.sort((one, two) => (one.ingoingFormat < two.ingoingFormat ? -1 : 1));
        } else if (filter.countClickIngoingFormat === 2) {
            documents.sort((one, two) => (one.ingoingFormat > two.ingoingFormat ? -1 : 1));
        }

        if (filter.countClickReceived === 1) {
            documents.sort((one, two) => (one.received < two.received ? -1 : 1));
        } else if (filter.countClickReceived === 2) {
            documents.sort((one, two) => (one.received > two.received ? -1 : 1));
        }

        if (filter.countClickIssued === 1) {
            documents.sort((one, two) => (one.issued < two.issued ? -1 : 1));
        } else if (filter.countClickIssued === 2) {
            documents.sort((one, two) => (one.issued > two.issued ? -1 : 1));
        }

        if (filter.countClickSenderName === 1) {
            documents.sort((one, two) => (one.senderName < two.senderName ? -1 : 1));
        } else if (filter.countClickSenderName === 2) {
            documents.sort((one, two) => (one.senderName > two.senderName ? -1 : 1));
        }

        return documents;
    }

    getOneDocument(id: number) : DocumentModel {
        let doc: DocumentModel = data.docs.find(k => k.id === id);
        if (doc != null) {
            return doc;
        } else {
            return null;
        }
    }

    generateEnumFields() : EnumFieldModel {
        let enumFields: EnumFieldModel = dataEnum.entityEnumInfo;
        return enumFields;
    }
}
