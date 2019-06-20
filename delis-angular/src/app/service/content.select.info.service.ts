import { Injectable } from '@angular/core';
import { HttpRestService } from './http.rest.service';
import { TableInfoModel } from '../models/table.info.model';
import { RuntimeConfigService } from './runtime.config.service';
import { LocalStorageService } from './local.storage.service';

@Injectable()
export class ContentSelectInfoService {

    constructor(
        private http: HttpRestService, private configService: RuntimeConfigService, private storage: LocalStorageService) {}

    generateAllContentSelectInfo(token: string) {
        this.http.methodGet(this.configService.getConfigUrl() + '/rest/table-info/enums', null, token).subscribe(
            (items: {}) => {
                this.setContent(items['items']);
            }
        );
    }

    generateUniqueOrganizationNameInfo(token: string) {
        this.http.methodGet(this.configService.getConfigUrl() + '/rest/table-info/organizations', null, token).subscribe(
            (data: {}) => {
                const organizations = data['data'];
                this.storage.set('organizations', organizations.uniqueOrganizationNames);
            }
        );
    }

    generateDateRangeInfo() {
        this.http.methodInnerGet('assets/config/date_range.json').subscribe(
            (data: {}) => {
                const dateRanges = data['range'];
                localStorage.setItem('dateRanges', JSON.stringify(dateRanges));
            }
        );

    }


    setContent(infoContent: TableInfoModel[]) {
        infoContent.forEach(info => {
            this.storage.set(info.entityName, info.entityEnumInfo);
        });
    }
}
