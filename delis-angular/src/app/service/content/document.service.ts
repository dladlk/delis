import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from '../system/token.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { HttpRestService } from '../system/http-rest.service';
import { DocumentFilterModel } from '../../model/filter/document-filter.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private readonly url: string;

  constructor(
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/document';
  }

  getListDocuments(currentPage: number, sizeElement: number, filter: DocumentFilterModel): Observable<any> {
    let params = this.generateParams(currentPage, sizeElement, filter);
    if (filter.dateReceived !== null) {
      params = params.append('createTime', String(new Date(filter.dateReceived.startDate).getTime()) + ':' + String(new Date(filter.dateReceived.endDate).getTime()));
    }
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getErrorListDocuments(currentPage: number, sizeElement: number, filter: DocumentFilterModel, errors: boolean): Observable<any> {
    let params = this.generateParams(currentPage, sizeElement, filter);
    params = params.append('flagParamErrorsDocument', 'FLAG_ERRORS_DOCUMENT');
    if (!errors) {
      let end = new Date();
      let start = new Date();
      start.setHours(end.getHours() - 1);
      params = params.append('createTime', String(start.getTime()) + ':' + String(end.getTime()));

    } else {
      if (filter.dateReceived !== null) {
        params = params.append('createTime', String(new Date(filter.dateReceived.startDate).getTime()) + ':' + String(new Date(filter.dateReceived.endDate).getTime()));
      }
    }

    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getOneDocumentById(id: any): Observable<any> {
    return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
  }

  getListDocumentBytesByDocumentId(id: any): Observable<any> {
    return this.httpRestService.methodGet(this.url + '/' + id + '/bytes', null, this.tokenService.getToken());
  }

  downloadFileByDocumentAndDocumentBytes(id: number, bytesId): Observable<any> {
    return this.httpRestService.downloadFileByDocumentAndDocumentBytes(this.url + '/download/' + id + '/bytes/' + bytesId, this.tokenService.getToken());
  }

  private generateParams(currentPage: number, sizeElement: number, filter: DocumentFilterModel): HttpParams {
    let params = new HttpParams();
    params = params.append('page', String(currentPage));
    params = params.append('size', String(sizeElement));
    params = params.append('sort', filter.sortBy);

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
    if (filter.organisation !== 'ALL') {
      params = params.append('organisation', filter.organisation);
    }
    if (filter.receiver !== null) {
      params = params.append('receiverIdentifier', filter.receiver);
    }
    if (filter.senderName !== null) {
      params = params.append('senderName', filter.senderName);
    }

    return params;
  }
}
