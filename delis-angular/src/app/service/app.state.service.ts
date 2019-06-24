import { Injectable } from "@angular/core";
import { StateSendDocumentsModel } from "../layout/content/send/models/state.send.documents.model";
import { StateDocumentsModel } from "../layout/content/documents/models/state.documents.model";
import { StateIdentifierModel } from "../layout/content/identifier/models/state.identifier.model";
import { StateDocumentsErrorModel } from "../layout/content/documents/models/state.documents.error.model";

@Injectable()
export class AppStateService {

    private STATE_DOCUMENT = 'stateDocument';
    private STATE_DOCUMENT_ERROR = 'stateDocumentError';
    private STATE_DOCUMENT_SEND = 'stateDocumentSend';
    private STATE_IDENTIFIER = 'stateIdentifier';

    getFilterDocumentState(): StateDocumentsModel {
        this.clearFilterDocumentSendState();
        this.clearFilterDocumentSErrorState();
        this.clearFilterIdentifierState();
        let filter = localStorage.getItem(this.STATE_DOCUMENT);
        if (filter === null) {
            return new StateDocumentsModel();
        } else {
            return JSON.parse(filter);
        }
    }

    getFilterDocumentErrorState(): StateDocumentsErrorModel {
        this.clearFilterDocumentState();
        this.clearFilterDocumentSendState();
        this.clearFilterIdentifierState();
        let filter = localStorage.getItem(this.STATE_DOCUMENT_ERROR);
        if (filter === null) {
            return new StateDocumentsErrorModel();
        } else {
            return JSON.parse(filter);
        }
    }

    getFilterDocumentSendState(): StateSendDocumentsModel {
        this.clearFilterDocumentState();
        this.clearFilterIdentifierState();
        this.clearFilterDocumentSErrorState();
        let filter = localStorage.getItem(this.STATE_DOCUMENT_SEND);
        if (filter === null) {
            return new StateSendDocumentsModel();
        } else {
            return JSON.parse(filter);
        }
    }

    getFilterIdentifierState(): StateIdentifierModel {
        this.clearFilterDocumentState();
        this.clearFilterDocumentSendState();
        this.clearFilterDocumentSErrorState();
        let filter = localStorage.getItem(this.STATE_IDENTIFIER);
        if (filter === null) {
            return new StateIdentifierModel();
        } else {
            return JSON.parse(filter);
        }
    }

    setFilterDocumentState(state: StateDocumentsModel) {
        localStorage.setItem(this.STATE_DOCUMENT, JSON.stringify(state));
    }

    setFilterDocumentErrorState(state: StateDocumentsErrorModel) {
        localStorage.setItem(this.STATE_DOCUMENT_ERROR, JSON.stringify(state));
    }

    setFilterDocumentSendState(state: StateSendDocumentsModel) {
        localStorage.setItem(this.STATE_DOCUMENT_SEND, JSON.stringify(state));
    }

    setFilterIdentifierState(state: StateIdentifierModel) {
        localStorage.setItem(this.STATE_IDENTIFIER, JSON.stringify(state));
    }

    clearFilterDocumentSendState() {
        localStorage.setItem(this.STATE_DOCUMENT_SEND, JSON.stringify(new StateSendDocumentsModel()));
    }

    clearFilterDocumentState() {
        localStorage.setItem(this.STATE_DOCUMENT, JSON.stringify(new StateDocumentsModel()));
    }

    clearFilterDocumentSErrorState() {
        localStorage.setItem(this.STATE_DOCUMENT_ERROR, JSON.stringify(new StateDocumentsErrorModel()));
    }

    clearFilterIdentifierState() {
        localStorage.setItem(this.STATE_IDENTIFIER, JSON.stringify(new StateIdentifierModel()));
    }
}
