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
        this.url = this.url + '/document';
    }

    getListDocuments(currentPage: number, sizeElement: number, filter: FilterProcessResult, errors: boolean) : Observable<any> {
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
        if (errors) {
            let end = new Date();
            let start = new Date();
            start.setHours(end.getHours() - 1);
            params = params.append('createTime', String(start.getTime()) + ':' + String(end.getTime()));
            params = params.append('flagParamErrorsDocument', 'FLAG_ERRORS_DOCUMENT');
        }
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

        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getOneDocumentById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }
}
