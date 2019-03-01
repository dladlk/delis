import { Injectable } from "@angular/core";
import { TokenService } from "../../../../../service/token.service";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";
import { Observable } from "rxjs";
import { HttpParams } from "@angular/common/http";

@Injectable()
export class ChartDocumentService {

    private url : string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/chart';
    }

    getChartData() : Observable<any> {
        return this.httpRestService.methodGet(this.url, null, this.tokenService.getToken());
    }

    getChartCustomData(date: Date[]) : Observable<any> {
        let params = new HttpParams();
        date[0].setHours(0,0,0,0);
        date[1].setHours(23,59,59,999);
        params = params.append('startDate', String(date[0].getTime()));
        params = params.append('endDate', String(date[1].getTime()));
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }
}
