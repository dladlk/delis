import { Injectable } from '@angular/core';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { HttpClient } from "@angular/common/http";

@Injectable()
export class DocumentsService {

    private documents: DocumentsModel[] = [];
    private startElement: number;

    constructor(private http: HttpClient) {
        this.http.get('http://localhost:8080/delis/rest/document').subscribe(res => this.documents = res["docs"]);
    }

    getDocuments() {
        return this.documents;
    }

    getDocumentsAfterFilter(currentPage: number, sizeElement: number, filter: FilterProcessResult) {

        console.log('http = ' + this.documents !== null ? this.documents.length : 'null');

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

    getCollectionSize() {
        return this.documents.length;
    }
}
