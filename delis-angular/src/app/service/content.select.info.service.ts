import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { TableInfoModel } from "../models/table.info.model";
import { RuntimeConfigService } from "./runtime.config.service";

@Injectable()
export class ContentSelectInfoService {

    constructor(private http: HttpRestService, private configService: RuntimeConfigService) {}

    generateAllContentSelectInfo() {
        this.http.methodOpenGet(this.configService.getConfigUrl() + '/rest/open/table-info/enums').subscribe(
            (items: {}) => {
                this.setContent(items["items"]);
            }
        )
    }

    generateUniqueOrganizationNameInfo() {
        this.http.methodOpenGet(this.configService.getConfigUrl() + '/rest/open/table-info/organizations').subscribe(
            (data: {}) => {
                let organizations = data["data"];
                localStorage.setItem("organizations", JSON.stringify(organizations.uniqueOrganizationNames))
            }
        )
    }

    generateDateRangeInfo() {
        this.http.methodInnerGet('assets/config/date_range.json').subscribe(
            (data: {}) => {
                let dateRanges = data["range"];
                localStorage.setItem("dateRanges", JSON.stringify(dateRanges))
            }
        );

    }


    setContent(infoContent: TableInfoModel[]) {
        infoContent.forEach(info => localStorage.setItem(info.entityName, JSON.stringify(info.entityEnumInfo)));
    }
}
