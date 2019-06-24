import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpParams } from "@angular/common/http";

import { TokenService } from "../../../../../service/token.service";
import { RuntimeConfigService } from "../../../../../service/runtime.config.service";
import { HttpRestService } from "../../../../../service/http.rest.service";
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

    getChartCustomData(drm: DateRangeModel, defaultChart: boolean) : Observable<any> {
        let params = new HttpParams();
        if (drm.dateStart !== null && drm.dateEnd !== null) {
            params = params.append('startDate', String(drm.dateStart.getTime()));
            params = params.append('endDate', String(drm.dateEnd.getTime()));
        }
        params = params.append('timeZone', Intl.DateTimeFormat().resolvedOptions().timeZone);
        params = params.append('defaultChart', String(defaultChart));
        return this.httpRestService.methodGet(this.url, params, this.tokenService.getToken());
    }
}
