import { JournalOrganisationFilterProcessResult } from "../models/journal.organisation.filter.process.result";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";

import { environment } from "../../../../../../environments/environment";
import { TokenService } from "../../../../../service/token.service";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";

@Injectable()
export class JournalOrganisationService {

    private env = environment;
    private url = this.env.api_url + '/journal/organisation';

    constructor(private http: HttpClient, private tokenService: TokenService, private configService: RuntimeConfigService, private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/journal/organisation';
    }

    getListJournalOrganisations(currentPage: number, sizeElement: number, filter: JournalOrganisationFilterProcessResult) : Observable<any> {

        let params = new HttpParams();

        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('sort', filter.sortBy);


        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
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

    getOneJournalOrganisationById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }
}
