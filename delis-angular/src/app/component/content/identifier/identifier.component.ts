import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { IDENTIFIER_PATH } from '../../../app.constants';

import { IdentifierService } from '../../../service/content/identifier.service';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { IdentifierDataSource } from './identifier-data-source';
import { IdentifierStateService } from "../../../service/state/identifier-state.service";

@Component({
  selector: 'app-identifier',
  templateUrl: './identifier.component.html',
  styleUrls: ['./identifier.component.scss']
})
export class IdentifierComponent implements OnInit {

  dataSource: IdentifierDataSource;
  organisations: string[] = [];
  statusList: EnumInfoModel[];
  selectedOrganisation: any;
  selectedStatusList: any;
  enumFilterModel: any;
  textFilterModel: any;
  BUNDLE_PREFIX = 'identifier.table.columnName.';
  IDENTIFIER_PATH = IDENTIFIER_PATH;


  constructor(
    private router: Router,
    private storage: LocalStorageService,
    public identifierService: IdentifierService,
    public identifierStateService: IdentifierStateService) { }

  ngOnInit() {
    this.initSelected();
    this.initEnumFilterModel();
    this.initTextFilterModel();
  }

  initSelected() {
    this.storage.select('Identifier', null).subscribe(enumInfo => {
      this.statusList = enumInfo.status;
      this.selectedStatusList = this.statusList[0];
    });
    this.storage.select('organizations', null).subscribe(organizationsInfo => {
      this.organisations = organizationsInfo;
      this.selectedOrganisation = this.organisations[0];
    });
  }

  initEnumFilterModel() {
    this.enumFilterModel = {
      organisation: {
        value: this.selectedOrganisation,
        list: this.organisations
      },
      status: {
        value: this.selectedStatusList,
        list: this.statusList
      }
    }
  }

  initTextFilterModel() {
    this.textFilterModel = {
      identifierGroup: null,
      type: null,
      value: null,
      uniqueValueType: null,
      name: null
    }
  }
}
