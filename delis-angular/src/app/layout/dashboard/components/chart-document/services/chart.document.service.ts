import { Injectable } from "@angular/core";
import { TokenService } from "../../../../../service/token.service";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";
import { Observable } from "rxjs";
import { HttpParams } from "@angular/common/http";
import { DateRangeModel } from "../../../../../models/date.range.model";

@Injectable()
export class ChartDocumentService {

    private url : string;

    constructor(
        private tokenService: TokenService,
        private configService: RuntimeConfigService,
        private httpRestService: HttpRestService) {
        this.url = this.configService.getConfigUrl();
        this.url = this.url + '/rest/chart';
    }

    getChartData() : Observable<any> {
        return this.httpRestService.methodGet(this.url, null, this.tokenService.getToken());
    }

    getChartCustomData(drm: DateRangeModel) : Observable<any> {
        let params = new HttpParams();
        params = params.append('startDate', String(drm.dateStart.getTime()));
        params = params.append('endDate', String(drm.dateEnd.getTime()));
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }
}
