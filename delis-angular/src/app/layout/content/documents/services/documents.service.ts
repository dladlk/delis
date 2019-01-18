import { Injectable } from '@angular/core';
import { FilterProcessResult } from '../models/documents.model';
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from "../../../../../environments/environment";
import { TokenService } from "../../../../service/token.service";

@Injectable()
export class DocumentsService {

    private headers: HttpHeaders;
    private env = environment;
    private url = this.env.api_url + '/document';

    constructor(private http: HttpClient, private tokenService: TokenService) {
        this.headers = new HttpHeaders({
            'Authorization' : tokenService.getToken()
        });
    }

    getListDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult) : Observable<any> {
        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('countClickReceiverIdentifier', String(filter.countClickReceiver));
        params = params.append('countClickDocumentStatus', String(filter.countClickStatus));
        params = params.append('countClickLastError', String(filter.countClickLastError));
        params = params.append('countClickDocumentType', String(filter.countClickDocumentType));
        params = params.append('countClickIngoingDocumentFormat', String(filter.countClickIngoingFormat));
        params = params.append('countClickCreateTime', String(filter.countClickReceived));
        params = params.append('countClickIssued', String(filter.countClickIssued));
        params = params.append('countClickSenderName', String(filter.countClickSenderName));
        params = params.append('countClickReceiverName', String(filter.countClickReceiverName));
        params = params.append('countClickOrganisation', String(filter.countClickOrganisation));
        if (filter.status !== 'ALL') {
            params = params.append('documentStatus', filter.status);
        }
        if (filter.lastError !== 'ALL') {
            params = params.append('lastError', filter.lastError);
        }
        if (filter.ingoingFormat !== 'ALL') {
            params = params.append('ingoingDocumentFormat', filter.ingoingFormat);
        }
        if (filter.documentType !== 'ALL') {
            params = params.append('documentType', filter.documentType);
        }
        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.receiver !== null) {
            params = params.append('receiverIdentifier', filter.receiver);
        }
        if (filter.senderName !== null) {
            params = params.append('senderName', filter.senderName);
        }
        if (filter.dateReceived !== null) {
            params = params.append('createTime', String(filter.dateReceived.dateStart.getTime()) + ':' + String(filter.dateReceived.dateEnd.getTime()));
        }

        return this.http.get(this.url + '', {headers : this.headers, params: params}).pipe(map(DocumentsService.extractData));
    }

    getOneDocumentById(id: any) : Observable<any> {
        return this.http.get(this.url + '/' + id, {headers : this.headers}).pipe(map(DocumentsService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
