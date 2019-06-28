import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { TokenService } from '../system/token.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { HttpRestService } from '../system/http-rest.service';
import { DocumentInvoiceResponseFormModel } from '../../model/content/document/document-invoice-response-form.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentInvoiceService {

  url: string;

  constructor(
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/document';
  }

  getInvoice(id: number): Observable<any> {
    return this.httpRestService.methodGet(this.url + '/' + id + '/invoice', null, this.tokenService.getToken());
  }

  generateAndSend(body: DocumentInvoiceResponseFormModel): Observable<any> {
    return this.httpRestService.methodPostModel(this.url + '/invoice/generate', body, this.tokenService.getToken());
  }

  generateAndDownloadFileByDocument(body: DocumentInvoiceResponseFormModel) {
    return this.httpRestService.generateAndDownloadFileByDocument(this.url + '/invoice/generate', body, this.tokenService.getToken());
  }
}
