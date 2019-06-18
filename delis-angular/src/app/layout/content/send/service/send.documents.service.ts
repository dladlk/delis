import {Injectable} from "@angular/core";
import {TokenService} from "../../../../service/token.service";
import {RuntimeConfigService} from "../../../../service/runtime.config.service";
import {HttpRestService} from "../../../../service/http.rest.service";
import {Observable} from "rxjs";
import {HttpParams} from "@angular/common/http";
import {SendDocumentsFilterProcessResult} from "../models/send.documents.filter.process.result";

@Injectable()
export class SendDocumentsService {

    private url: string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/document/send';
    }

    getListSendDocuments(currentPage: number, sizeElement: number, filter: SendDocumentsFilterProcessResult): Observable<any> {

        let params = new HttpParams();

        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('sort', filter.sortBy);
        if (filter.documentStatus !== 'ALL') {
            params = params.append('documentStatus', filter.documentStatus);
        }
        if (filter.documentType !== 'ALL') {
            params = params.append('documentType', filter.documentType);
        }
        if (filter.receiverIdRaw !== null) {
            params = params.append('receiverIdRaw', filter.receiverIdRaw);
        }
        if (filter.senderIdRaw !== null) {
            params = params.append('senderIdRaw', filter.senderIdRaw);
        }
        if (filter.documentId !== null) {
            params = params.append('documentId', filter.documentId);
        }
        if (filter.documentDate !== null) {
            params = params.append('documentDate', filter.documentDate);
        }
        if (filter.sentMessageId !== null) {
            params = params.append('sentMessageId', filter.sentMessageId);
        }
        if (filter.dateRange !== null) {
            filter.dateRange.dateStart.setHours(0,0,0,0);
            filter.dateRange.dateEnd.setHours(23,59,59,999);
            params = params.append('createTime', String(filter.dateRange.dateStart.getTime()) + ':' + String(filter.dateRange.dateEnd.getTime()));
        }

        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getOneSendDocumentsById(id: any): Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }
}
