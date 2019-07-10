import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { SendDocumentService } from '../../../service/content/send-document.service';
import { SendDocumentDataSource } from './send-document-data-source';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { SendDocumentStateService } from "../../../service/state/send-document-state.service";
import { SEND_DOCUMENT_PATH } from "../../../app.constants";

@Component({
  selector: 'app-send-document',
  templateUrl: './send-document.component.html',
  styleUrls: ['./send-document.component.scss']
})
export class SendDocumentComponent implements OnInit {

  dataSource: SendDocumentDataSource;

  organisations: string[] = [];
  statuses: EnumInfoModel[] = [];
  documentTypes: EnumInfoModel[] = [];

  selectedDocumentType: any;
  selectedStatus: any;
  selectedOrganisation: any;

  enumFilterModel: any;
  textFilterModel: any;

  BUNDLE_PREFIX = 'documents.table.send.columnName.';
  SEND_DOCUMENT_PATH = SEND_DOCUMENT_PATH;

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    private sendDocumentService: SendDocumentService,
    private sendDocumentStateService: SendDocumentStateService) { }

  ngOnInit() {
    this.initSelected();
    this.initEnumFilterModel();
    this.initTextFilterModel();
  }

  initSelected() {
    this.storage.select('SendDocument', null).subscribe(enumInfo => {
      this.statuses = enumInfo.documentStatus;
      this.selectedStatus = this.statuses[0];
      this.documentTypes = enumInfo.documentType;
      this.selectedDocumentType = this.documentTypes[0];
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
      documentStatus: {
        value: this.selectedStatus,
        list: this.statuses
      },
      documentType: {
        value: this.selectedDocumentType,
        list: this.documentTypes
      }
    }
  }

  initTextFilterModel() {
    this.textFilterModel = {
      receiverIdRaw: null,
      senderIdRaw: null
    }
  }
}
