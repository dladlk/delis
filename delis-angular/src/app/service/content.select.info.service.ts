import { Injectable } from "@angular/core";
import { HttpRestService } from "./http.rest.service";
import { TableInfoModel } from "../models/table.info.model";

@Injectable()
export class ContentSelectInfoService {

    constructor(private http: HttpRestService) {}

    generateAllContentSelectInfo(url: string, token: string) {
        this.http.methodGet(url, null, token).subscribe(
            (items: {}) => {
                this.setContent(items["items"]);
            }
        )
    }

    setContent(infoContent: TableInfoModel[]) {
        infoContent.forEach(info => localStorage.setItem(info.entityName, JSON.stringify(info.entityEnumInfo)));
    }
}
