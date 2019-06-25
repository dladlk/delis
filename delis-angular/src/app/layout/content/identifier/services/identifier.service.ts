import { Injectable } from "@angular/core";
import { HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { TokenService } from "../../../../service/token.service";
import { IdentifierFilterProcessResult } from "../models/identifier.filter.process.result";
import { RuntimeConfigService } from "../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../service/http.rest.service";

@Injectable()
export class IdentifierService {

    private url : string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/identifier';
    }

    getListIdentifiers(currentPage: number, sizeElement: number, filter: IdentifierFilterProcessResult) : Observable<any> {

        let params = new HttpParams();

        params = params.append('page', String(currentPage));
        params = params.append('size', String(sizeElement));
        params = params.append('sort', filter.sortBy);
        if (filter.status !== 'ALL') {
            params = params.append('status', filter.status);
        }
        if (filter.publishingStatus !== 'ALL') {
            params = params.append('publishingStatus', filter.publishingStatus);
        }
        if (filter.organisation !== null) {
            params = params.append('organisation', filter.organisation);
        }
        if (filter.identifierGroup !== null) {
            params = params.append('identifierGroup', filter.identifierGroup);
        }
        if (filter.type !== null) {
            params = params.append('type', filter.type);
        }
        if (filter.value !== null) {
            params = params.append('value', filter.value);
        }
        if (filter.uniqueValueType !== null) {
            params = params.append('uniqueValueType', filter.uniqueValueType);
        }
        if (filter.name !== null) {
            params = params.append('name', filter.name);
        }
        if (filter.dateRange !== null) {
            params = params.append('createTime', String(new Date(filter.dateRange.startDate).getTime()) + ':' + String(new Date(filter.dateRange.endDate).getTime()));

        }

        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getOneIdentifierById(id: any) : Observable<any> {
        return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
    }
}
