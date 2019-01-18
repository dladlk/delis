import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { environment } from "../../../../../../environments/environment";
import { TokenService } from "../../../../../service/token.service";
import { JournalDocumentFilterProcessResult } from "../models/journal.document.filter.process.result";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Injectable()
export class JournalDocumentService {

    private headers: HttpHeaders;
    private env = environment;
    private url = this.env.api_url + '/journal/document';

    constructor(private http: HttpClient, private tokenService: TokenService) {
        this.headers = new HttpHeaders({
            'Authorization' : tokenService.getToken()
        });
    }

    getListJournalDocuments(currentPage: number, sizeElement: number, filter: JournalDocumentFilterProcessResult) : Observable<any> {

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

        return this.http.get(this.url + '', {headers : this.headers, params: params}).pipe(map(JournalDocumentService.extractData));
    }

    getOneJournalDocumentById(id: any) : Observable<any> {
        return this.http.get(this.url + '/' + id, {headers : this.headers}).pipe(map(JournalDocumentService.extractData));
    }

    private static extractData(res: Response) {
        return res || { };
    }
}
