import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {TokenService} from '../../../../service/token.service';
import {RuntimeConfigService} from '../../../../service/runtime.config.service';
import {HttpRestService} from '../../../../service/http.rest.service';
import {DocumentInvoiceResponseFormModel} from '../models/document.invoice.response.form.model';

@Injectable()
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
