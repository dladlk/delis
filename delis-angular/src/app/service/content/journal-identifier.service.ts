import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RuntimeConfigService } from '../system/runtime-config.service';
import { HttpRestService } from '../system/http-rest.service';
import { TokenService } from '../system/token.service';
import { JournalIdentifierFilterModel } from '../../model/filter/journal-identifier-filter.model';

@Injectable({
  providedIn: 'root'
})
export class JournalIdentifierService {

  private readonly url: string;

  constructor(private configService: RuntimeConfigService,
              private httpRestService: HttpRestService, private tokenService: TokenService) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/journal/identifier';
  }

  getAllByIdentifierId(identifierId: any, filter: JournalIdentifierFilterModel): Observable<any> {
    let params = new HttpParams();
    params = params.append('sort', filter.sortBy);
    return this.httpRestService.methodGetOneById(this.url + '/one', params, this.tokenService.getToken(), identifierId);
  }
}
