import { Injectable } from '@angular/core';
import { TableInfoModel } from '../../model/system/table-info.model';
import { HttpRestService } from './http-rest.service';
import { RuntimeConfigService } from './runtime-config.service';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ContentSelectInfoService {

  constructor(
    private http: HttpRestService, private configService: RuntimeConfigService, private storage: LocalStorageService) {}

  generateAllContentSelectInfo(token: string) {
    this.http.methodGet(this.configService.getConfigUrl() + '/rest/table-info/enums', null, token).subscribe(
      (items: {items: any}) => {
        this.setContent(items.items);
      }
    );
  }

  generateUniqueOrganizationNameInfo(token: string) {
    this.http.methodGet(this.configService.getConfigUrl() + '/rest/table-info/organizations', null, token).subscribe(
      (data: {data: any}) => {
        const organizations = data.data;
        this.storage.set('organizations', organizations.uniqueOrganizationNames);
      }
    );
  }

  setContent(infoContent: TableInfoModel[]) {
    infoContent.forEach(info => {
      this.storage.set(info.entityName, info.entityEnumInfo);
    });
  }
}
