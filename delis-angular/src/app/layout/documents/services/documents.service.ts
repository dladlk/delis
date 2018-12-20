import { Injectable } from '@angular/core';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as data from '../documents.json';

@Injectable()
export class DocumentsService {

    private documents: DocumentsModel[] = [];
    private startElement: number;

    constructor(private http: HttpClient) {
    }

    getAnyDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult) : Observable<any> {
        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('countClickOrganisation', String(filter.countClickOrganisation));
        params = params.append('countClickReceiver', String(filter.countClickReceiver));
        params = params.append('countClickStatus', String(filter.countClickStatus));
        params = params.append('countClickLastError', String(filter.countClickLastError));
        params = params.append('countClickDocumentType', String(filter.countClickDocumentType));
        params = params.append('countClickIngoingFormat', String(filter.countClickIngoingFormat));
        params = params.append('countClickReceived', String(filter.countClickReceived));
        params = params.append('countClickIssued', String(filter.countClickIssued));
        params = params.append('countClickSenderName', String(filter.countClickSenderName));
        params = params.append('countClickReceiverName', String(filter.countClickReceiverName));
        if (filter.status !== 'ALL') {
            params = params.append('status', filter.status);
        }
        if (filter.lastError !== 'ALL') {
            params = params.append('lastError', filter.lastError);
        }
        if (filter.ingoingFormat !== 'ALL') {
            params = params.append('ingoingFormat', filter.ingoingFormat);
        }
        if (filter.documentType !== 'ALL') {
            params = params.append('documentType', filter.documentType);
        }
        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.receiver !== null) {
            params = params.append('receiver', filter.receiver);
        }
        if (filter.senderName !== null) {
            params = params.append('senderName', filter.senderName);
        }
        if (filter.receiverName !== null) {
            params = params.append('receiverName', filter.receiverName);
        }
        let url = 'http://localhost:8080/delis/rest/document';
        return this.http.get(url, {params: params}).pipe(map(DocumentsService.extractData));
    }

    getDocuments() {
        this.documents = data.docs;
        return this.documents;
    }

    getDocumentsAfterFilter(currentPage: number, sizeElement: number, filter: FilterProcessResult) {

        this.startElement = currentPage * sizeElement;
        this.getDocuments();

        if (filter.status !== 'ALL') {
            this.documents = this.documents.filter(el => el.status === filter.status);
        }

        if (filter.lastError !== 'ALL') {
            this.documents = this.documents.filter(el => el.lastError === filter.lastError);
        }

        if (filter.documentType !== 'ALL') {
            this.documents = this.documents.filter(el => el.documentType === filter.documentType);
        }

        if (filter.ingoingFormat !== 'ALL') {
            this.documents = this.documents.filter(el => el.ingoingFormat === filter.ingoingFormat);
        }

        return this.documents.slice(this.startElement, this.startElement + sizeElement);
    }

    private static extractData(res: Response) {
        return res || { };
    }

    getCollectionSize() {
        return this.documents.length;
    }
}
