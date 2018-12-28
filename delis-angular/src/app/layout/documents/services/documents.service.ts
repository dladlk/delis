import { Injectable } from '@angular/core';
import { FilterProcessResult } from '../models/documents.model';
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from "../../../../environments/environment";

@Injectable()
export class DocumentsService {

    private env = environment;
    private url = this.env.api_url + '/rest/document';

    constructor(private http: HttpClient) {}

    getListDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult) : Observable<any> {
        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('countClickReceiver', String(filter.countClickReceiver));
        params = params.append('countClickStatus', String(filter.countClickStatus));
        params = params.append('countClickLastError', String(filter.countClickLastError));
        params = params.append('countClickDocumentType', String(filter.countClickDocumentType));
        params = params.append('countClickIngoingFormat', String(filter.countClickIngoingFormat));
        params = params.append('countClickReceived', String(filter.countClickReceived));
        params = params.append('countClickIssued', String(filter.countClickIssued));
        params = params.append('countClickSenderName', String(filter.countClickSenderName));
        params = params.append('countClickReceiverName', String(filter.countClickReceiverName));
        params = params.append('countClickOrganisation', String(filter.countClickOrganisation));
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

        return this.http.get(this.url + '', {params: params}).pipe(map(DocumentsService.extractData));
    }

    getOneDocumentById(id: any) : Observable<any> {
        return this.http.get(this.url + '/' + id).pipe(map(DocumentsService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
