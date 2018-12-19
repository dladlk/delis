import { Injectable } from '@angular/core';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class DocumentsService {

    private documents: DocumentsModel[] = [];
    private startElement: number;

    constructor(private http: HttpClient) {
    }

    getAnyDocuments(currentPage: number, sizeElement: number) : Observable<any> {
        let url = 'http://localhost:8080/delis/rest/document' + '?page=' + currentPage + '&size=' + sizeElement;
        console.log('url = ' + url);
        return this.http.get(url).pipe(map(DocumentsService.extractData));
    }

    getDocumentsAfterFilter(currentPage: number, sizeElement: number, filter: FilterProcessResult) {

        this.startElement = currentPage * sizeElement;
        // this.getDocuments();

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
}
