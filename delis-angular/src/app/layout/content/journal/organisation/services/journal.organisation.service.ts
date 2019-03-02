import { JournalOrganisationFilterProcessResult } from "../models/journal.organisation.filter.process.result";
import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "../../../../../../environments/environment";
import { TokenService } from "../../../../../service/token.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Injectable()
export class JournalOrganisationService {

    private headers: HttpHeaders;
    private env = environment;
    private url = this.env.api_url + '/journal/organisation';
    private config: string;

    constructor(private http: HttpClient, private tokenService: TokenService) {
        this.headers = new HttpHeaders({
            'Authorization' : tokenService.getToken()
        });
        this.config = localStorage.getItem('url');
        if (this.config !== '${API_URL}') {
            this.url = this.config + '/journal/organisation';
        }
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

        return this.http.get(this.url + '', {headers : this.headers, params: params}).pipe(map(JournalOrganisationService.extractData));
    }

    getOneJournalOrganisationById(id: any) : Observable<any> {
        return this.http.get(this.url + '/' + id, {headers : this.headers}).pipe(map(JournalOrganisationService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
