import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { TokenService } from '../system/token.service';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { HttpRestService } from '../system/http-rest.service';

@Injectable({
  providedIn: 'root'
})
export class JournalDocumentService {

  private readonly url: string;

  constructor(private configService: RuntimeConfigService,
              private httpRestService: HttpRestService, private tokenService: TokenService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/journal/document';
  }

  getAllByDocumentId(documentId: any): Observable<any> {
    let params = new HttpParams();
    params = params.append('sort', 'orderBy_Id_Desc');
    return this.httpRestService.methodGetOneById(this.url + '/one', params, this.tokenService.getToken(), documentId);
  }

  getByJournalDocumentDocumentId(documentId: any): Observable<any> {
    return this.httpRestService.methodGetOneById(this.url + '/one/error', null, this.tokenService.getToken(), documentId);
  }
}
