import { Injectable} from '@angular/core';
import { HttpParams} from '@angular/common/http';
import { Observable} from 'rxjs';

import { HttpRestService } from '../system/http-rest.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { TokenService } from '../system/token.service';
import { SendDocumentFilterModel } from '../../model/filter/send-document-filter.model';

@Injectable({
  providedIn: 'root'
})
export class SendDocumentService {

  private readonly url: string;

  constructor(
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/document/send';
  }

  getListSendDocuments(currentPage: number, sizeElement: number, filter: SendDocumentFilterModel): Observable<any> {

    let params = new HttpParams();

    params = params.append('page', String(currentPage));
    params = params.append('size', String(sizeElement));
    params = params.append('sort', filter.sortBy);
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
      params = params.append('createTime', String(new Date(filter.dateRange.fromDate).getTime()) + ':' + String(new Date(filter.dateRange.toDate).getTime()));
    }

    return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
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
    return this.httpRestService.downloadFileByDocumentAndDocumentBytes(this.url + '/download/' + id + '/bytes/' + bytesId, this.tokenService.getToken());
  }
}
