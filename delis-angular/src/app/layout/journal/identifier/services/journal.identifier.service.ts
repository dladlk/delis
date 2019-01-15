import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "../../../../../environments/environment";
import { TokenService } from "../../../../service/token.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { JournalIdentifierFilterProcessResultModel } from "../models/journal.identifier.filter.process.result.model";

@Injectable()
export class JournalIdentifierService {

    private headers: HttpHeaders;
    private env = environment;
    private url = this.env.api_url + '/journal/identifier';

    constructor(private http: HttpClient, private tokenService: TokenService) {
        this.headers = new HttpHeaders({
            'Authorization' : tokenService.getToken()
        });
    }

    getListJournalIdentifiers(currentPage: number, sizeElement: number, filter: JournalIdentifierFilterProcessResultModel) : Observable<any> {

        let params = new HttpParams();

        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));

        params = params.append('countClickOrganisation', String(filter.countClickOrganisation));
        params = params.append('countClickIdentifier', String(filter.countClickIdentifier));
        params = params.append('countClickCreateTime', String(filter.countClickCreateTime));
        params = params.append('countClickMessage', String(filter.countClickMessage));
        params = params.append('countClickDurationMs', String(filter.countClickDurationMs));

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
            params = params.append('start', String(filter.dateRange.dateStart.getTime()));
            params = params.append('end', String(filter.dateRange.dateEnd.getTime()));
        }

        return this.http.get(this.url + '', {headers : this.headers, params: params}).pipe(map(JournalIdentifierService.extractData));
    }

    getOneJournalIdentifierById(id: any) : Observable<any> {
        return this.http.get(this.url + '/' + id, {headers : this.headers}).pipe(map(JournalIdentifierService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}