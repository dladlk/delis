import {Injectable} from '@angular/core';
import {HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

import {TokenService} from '../system/token.service';
import {RuntimeConfigService} from '../system/runtime-config.service';
import {HttpRestService} from '../system/http-rest.service';
import {DelisService} from './delis-service';
import {DocumentFilterModel} from '../../model/filter/document-filter.model';
import {DocumentModel} from '../../model/content/document/document.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentService implements DelisService<DocumentModel, DocumentFilterModel> {

  private readonly url: string;

  constructor(private tokenService: TokenService, private configService: RuntimeConfigService, private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/document';
  }

  private static generateParams(filter: DocumentFilterModel): HttpParams {
    let params = filter.toHttpParams();

    if (filter.documentStatus !== 'ALL') {
      if (filter.documentStatus === 'ALL FAILED') {
        params = params.append('statusError', String('true'));
      } else {
        params = params.append('documentStatus', filter.documentStatus);
      }
    }
    if (filter.lastError !== 'ALL') {
      params = params.append('lastError', filter.lastError);
    }
    if (filter.ingoingDocumentFormat !== 'ALL') {
      params = params.append('ingoingDocumentFormat', filter.ingoingDocumentFormat);
    }
    if (filter.documentType !== 'ALL') {
      params = params.append('documentType', filter.documentType);
    }
    if (filter.organisation !== 'ALL') {
      params = params.append('organisation', filter.organisation);
    }
    if (filter.receiverIdentifier !== null) {
      params = params.append('receiverIdentifier', filter.receiverIdentifier);
    }
    if (filter.senderName !== null) {
      params = params.append('senderName', filter.senderName);
    }

    return params;
  }

  getAll(filter: DocumentFilterModel): Observable<any> {
    let params = DocumentService.generateParams(filter);
    if (filter.dateRange !== null) {
      const value = String(new Date(filter.dateRange.fromDate).getTime()) + ':' + String(new Date(filter.dateRange.toDate).getTime());
      params = params.append('createTime', value);
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
    const url = this.url + '/download/' + id + '/bytes/' + bytesId;
    return this.httpRestService.downloadFileByDocumentAndDocumentBytes(url, this.tokenService.getToken());
  }
}
