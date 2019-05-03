import { Injectable } from '@angular/core';
import { FilterProcessResult } from '../models/filter.process.result';
import { HttpParams } from "@angular/common/http";
import { Observable } from 'rxjs';
import { RuntimeConfigService } from "../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../service/http.rest.service";
import { TokenService } from "../../../../service/token.service";

@Injectable()
export class DocumentsService {

    private url : string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/document';
    }

    getListDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult) : Observable<any> {
        let params = this.generateParams(currentPage, sizeElement, filter);
        if (filter.dateReceived !== null) {
            filter.dateReceived.dateStart.setHours(0,0,0,0);
            filter.dateReceived.dateEnd.setHours(23,59,59,999);
            params = params.append('createTime', String(filter.dateReceived.dateStart.getTime()) + ':' + String(filter.dateReceived.dateEnd.getTime()));
        }
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getErrorListDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult, errors: boolean) : Observable<any> {
        let params = this.generateParams(currentPage, sizeElement, filter);
        params = params.append('flagParamErrorsDocument', 'FLAG_ERRORS_DOCUMENT');
        if (!errors) {
            let end = new Date();
            let start = new Date();
            start.setHours(end.getHours() - 1);
            params = params.append('createTime', String(start.getTime()) + ':' + String(end.getTime()));

        } else {
            if (filter.dateReceived !== null) {
                filter.dateReceived.dateStart.setHours(0,0,0,0);
                filter.dateReceived.dateEnd.setHours(23,59,59,999);
                params = params.append('createTime', String(filter.dateReceived.dateStart.getTime()) + ':' + String(filter.dateReceived.dateEnd.getTime()));
            }
        }

        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getOneDocumentById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }

    getListDocumentBytesByDocumentId(id: any) : Observable<any> {
        return this.httpRestService.methodGet(this.url + '/' + id + '/bytes', null, this.tokenService.getToken());
    }

    private generateParams(currentPage: number, sizeElement: number, filter: FilterProcessResult) : HttpParams {
        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('sort', filter.sortBy);

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
        if (filter.organisation !== 'ALL') {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.receiver !== null) {
            params = params.append('receiverIdentifier', filter.receiver);
        }
        if (filter.senderName !== null) {
            params = params.append('senderName', filter.senderName);
        }

        return params;
    }
}
