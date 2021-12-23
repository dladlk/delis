import { Injectable} from '@angular/core';
import { HttpParams} from '@angular/common/http';
import { Observable} from 'rxjs';

import { HttpRestService } from '../system/http-rest.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { TokenService } from '../system/token.service';
import { SendDocumentFilterModel } from '../../model/filter/send-document-filter.model';
import { DelisService } from './delis-service';
import { SendDocumentModel } from '../../model/content/send-document/send-document.model';

@Injectable({
  providedIn: 'root'
})
export class SendDocumentService implements DelisService<SendDocumentModel, SendDocumentFilterModel> {

  private readonly url: string;

  constructor(
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/document/send';
  }

  getAll(filter: SendDocumentFilterModel): Observable<any> {
    const params = this.generateParams(filter);
    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
  }

  getAllIds(filter: SendDocumentFilterModel): Observable<any> {
    const params = this.generateParams(filter);
    return this.httpRestService.methodGet(this.url + '/next', params, this.tokenService.getToken());
  }

  getOneSendDocumentsById(id: any): Observable<any> {
    return this.httpRestService.methodGetOne(this.url, id, this.tokenService.getToken());
  }

  getListSendDocumentBytesBySendDocumentId(id: any): Observable<any> {
    return this.httpRestService.methodGet(this.url + '/' + id + '/bytes', null, this.tokenService.getToken());
  }

  getListJournalSendDocumentBySendDocumentId(id: any): Observable<any> {
    return this.httpRestService.methodGet(this.url + '/' + id + '/journal', null, this.tokenService.getToken());
  }

  downloadFileBySendDocumentAndDocumentBytes(id: number, bytesId): Observable<any> {
    const url = this.url + '/download/' + id + '/bytes/' + bytesId;
    return this.httpRestService.downloadFileByDocumentAndDocumentBytes(url, this.tokenService.getToken());
  }

  private generateParams(filter: SendDocumentFilterModel): HttpParams {
    let params = new HttpParams();

    params = params.append('page', String(filter.pageIndex));
    params = params.append('size', String(filter.pageSize));
    params = params.append('sort', filter.sort.active);
    params = params.append('order', filter.sort.direction);
    if (filter.organisation !== 'ALL') {
      params = params.append('organisation', filter.organisation);
    }
    if (filter.documentStatus !== 'ALL') {
      params = params.append('documentStatus', filter.documentStatus);
    }
    if (filter.documentType !== 'ALL') {
      params = params.append('documentType', filter.documentType);
    }
    if (filter.receiverIdRaw !== null) {
      params = params.append('receiverIdRaw', filter.receiverIdRaw);
    }
    if (filter.senderIdRaw !== null) {
      params = params.append('senderIdRaw', filter.senderIdRaw);
    }
    if (filter.documentId !== null) {
      params = params.append('documentId', filter.documentId);
    }
    if (filter.documentDate !== null) {
      params = params.append('documentDate', filter.documentDate);
    }
    if (filter.sentMessageId !== null) {
      params = params.append('sentMessageId', filter.sentMessageId);
    }
    if (filter.dateRange !== null) {
      const value = String(new Date(filter.dateRange.fromDate).getTime()) + ':' + String(new Date(filter.dateRange.toDate).getTime());
      params = params.append('createTime', value);
    }
    return params;
  }
}
