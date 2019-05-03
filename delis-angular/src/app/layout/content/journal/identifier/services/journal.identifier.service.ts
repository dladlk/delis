import { Injectable } from "@angular/core";
import { HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

import { TokenService } from "../../../../../service/token.service";
import { JournalIdentifierFilterProcessResultModel } from "../models/journal.identifier.filter.process.result.model";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";

@Injectable()
export class JournalIdentifierService {

    private url : string;

    constructor(private configService: RuntimeConfigService,
                private httpRestService: HttpRestService, private tokenService: TokenService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/journal/identifier';
    }

    getListJournalIdentifiers(currentPage: number, sizeElement: number, filter: JournalIdentifierFilterProcessResultModel) : Observable<any> {

        let params = new HttpParams();
        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('sort', filter.sortBy);

        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.identifier !== null) {
            params = params.append('identifier', filter.identifier);
        }
        if (filter.message !== null) {
            params = params.append('message', filter.message);
        }
        if (filter.durationMs !== null) {
            params = params.append('durationMs', String(filter.durationMs));
        }
        if (filter.dateRange !== null) {
            filter.dateRange.dateStart.setHours(0,0,0,0);
            filter.dateRange.dateEnd.setHours(23,59,59,999);
            params = params.append('createTime', String(filter.dateRange.dateStart.getTime()) + ':' + String(filter.dateRange.dateEnd.getTime()));
        }

        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getOneJournalIdentifierById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }

    getAllByIdentifierId(identifierId: any, filter: JournalIdentifierFilterProcessResultModel) : Observable<any> {
        let params = new HttpParams();
        params = params.append('sort', filter.sortBy);
        return this.httpRestService.methodGetOneById(this.url + '/one', params, this.tokenService.getToken(), identifierId);
    }
}
