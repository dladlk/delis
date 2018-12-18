import { Injectable } from '@angular/core';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { HttpClient } from "@angular/common/http";
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable()
export class DocumentsService {

    private documents: DocumentsModel[] = [];
    private startElement: number;

    constructor(private http: HttpClient) {}

    getDocuments()  {
        return this.http.get('http://localhost:8080/delis/rest/document').pipe(map(this.extractData)).subscribe((data: {}) => {
            console.log(data);
            this.documents = data["items"];
        });
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

    getCollectionSize() {
        return this.documents.length;
    }

    private extractData(res: Response) {
        let body = res;
        return body || { };
    }
}
