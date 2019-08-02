import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { TokenService } from '../system/token.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { HttpRestService } from '../system/http-rest.service';
import { IdentifierFilterModel } from '../../model/filter/identifier-filter.model';
import { DelisService } from "./delis-service";
import { IdentifierModel } from "../../model/content/identifier/identifier.model";

@Injectable({
  providedIn: 'root'
})
export class IdentifierService implements DelisService<IdentifierModel, IdentifierFilterModel> {

  private readonly url: string;

  constructor(
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/identifier';
  }

  getAll(filter: IdentifierFilterModel): Observable<any> {

    let params = new HttpParams();

    params = params.append('page', String(filter.pageIndex));
    params = params.append('size', String(filter.pageSize));
    params = params.append('sort', filter.sort.active);
    params = params.append('order', filter.sort.direction);
    if (filter.status !== 'ALL') {
      params = params.append('status', filter.status);
    }
    if (filter.publishingStatus !== 'ALL') {
      params = params.append('publishingStatus', filter.publishingStatus);
    }
    if (filter.organisation !== 'ALL') {
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
    if (filter.name !== null) {
      params = params.append('name', filter.name);
    }
    if (filter.dateRange !== null) {
      params = params.append('createTime', String(new Date(filter.dateRange.fromDate).getTime()) + ':' + String(new Date(filter.dateRange.toDate).getTime()));
    }

    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getOneIdentifierById(id: any): Observable<any> {
    return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
  }
}
