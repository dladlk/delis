import { Injectable } from "@angular/core";
import { HttpParams } from "@angular/common/http";
import { TokenService } from "../../../../../service/token.service";
import { JournalDocumentFilterProcessResult } from "../models/journal.document.filter.process.result";
import { Observable } from "rxjs";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";

@Injectable()
export class JournalDocumentService {

    private url : string;

    constructor(private configService: RuntimeConfigService,
                private httpRestService: HttpRestService, private tokenService: TokenService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/journal/document';
    }

    getListJournalDocuments(currentPage: number, sizeElement: number, filter: JournalDocumentFilterProcessResult) : Observable<any> {

        let params = this.generateParams(currentPage, sizeElement, filter);

        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.document !== null) {
            params = params.append('document', filter.document);
        }
        if (filter.type !== 'ALL') {
            params = params.append('type', filter.type);
        }
        if (filter.success !== 'ALL') {
            params = params.append('success', filter.success);
        }
        if (filter.message !== null) {
            params = params.append('message', filter.message);
        }
        if (filter.durationMs !== null) {
            params = params.append('durationMs', String(filter.durationMs));
        }
        if (filter.dateRange !== null) {
            params = params.append('createTime', String(filter.dateRange.dateStart.getTime()) + ':' + String(filter.dateRange.dateEnd.getTime()));
        }

        return this.httpRestService.methodGet(this.url, {params: params}, this.tokenService.getToken());
    }

    getOneJournalDocumentById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }

    getAllByDocumentId(documentId: any, currentPage: number, sizeElement: number, filter: JournalDocumentFilterProcessResult) : Observable<any> {
        let params = this.generateParams(currentPage, sizeElement, filter);
        return this.httpRestService.methodGetOneById(this.url + '/one', {params: params}, this.tokenService.getToken(), documentId);
    }

    private generateParams(currentPage: number, sizeElement: number, filter: JournalDocumentFilterProcessResult) : HttpParams {
        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('countClickOrganisation', String(filter.countClickOrganisation));
        params = params.append('countClickDocument', String(filter.countClickDocument));
        params = params.append('countClickCreateTime', String(filter.countClickCreateTime));
        params = params.append('countClickType', String(filter.countClickDocumentProcessStepType));
        params = params.append('countClickSuccess', String(filter.countClickSuccess));
        params = params.append('countClickMessage', String(filter.countClickMessage));
        params = params.append('countClickDurationMs', String(filter.countClickDurationMs));
        return params;
    }
}
