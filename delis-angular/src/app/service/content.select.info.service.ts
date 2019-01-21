import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { TableInfoModel } from "../models/table.info.model";
import { RuntimeConfigService } from "./runtime.config.service";

@Injectable()
export class ContentSelectInfoService {

    constructor(private http: HttpRestService, private configService: RuntimeConfigService) {}

    generateAllContentSelectInfo() {
        this.http.methodOpenGet(this.configService.getConfigUrl() + '/table-info').subscribe(
            (items: {}) => {
                this.setContent(items["items"]);
            }
        )
    }

    setContent(infoContent: TableInfoModel[]) {
        infoContent.forEach(info => localStorage.setItem(info.entityName, JSON.stringify(info.entityEnumInfo)));
    }
}
