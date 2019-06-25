import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpParams } from "@angular/common/http";

import { TokenService } from "../../../../../service/token.service";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";
import { DateRangePicker } from "../../../../bs-component/components/daterange/date.range.picker";

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

    getChartCustomData(drm: DateRangePicker, defaultChart: boolean) : Observable<any> {
        let params = new HttpParams();
        params = params.append('startDate', String(new Date(drm.startDate).getTime()));
        params = params.append('endDate', String(new Date(drm.endDate).getTime()));
        params = params.append('timeZone', Intl.DateTimeFormat().resolvedOptions().timeZone);
        params = params.append('defaultChart', String(defaultChart));
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }

    getChartDefaultData(start: Date, end: Date, defaultChart: boolean) : Observable<any> {
        let params = new HttpParams();
        params = params.append('startDate', String(start.getTime()));
        params = params.append('endDate', String(end.getTime()));
        params = params.append('timeZone', Intl.DateTimeFormat().resolvedOptions().timeZone);
        params = params.append('defaultChart', String(defaultChart));
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }
}
