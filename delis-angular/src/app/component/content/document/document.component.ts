import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { DOCUMENT_PATH } from '../../../app.constants';

import { DocumentDataSource } from './document-data-source';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { DocumentService } from '../../../service/content/document.service';
import { DocumentStateService } from '../../../service/state/document-state.service';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  dataSource: DocumentDataSource;
  DOCUMENT_PATH = DOCUMENT_PATH;

  statuses: EnumInfoModel[] = [];
  documentTypes: EnumInfoModel[] = [];
  ingoingFormats: EnumInfoModel[] = [];
  lastErrors: EnumInfoModel[] = [];
  organisations: string[] = [];

  selectedStatus: any;
  selectedDocumentType: any;
  selectedIngoingFormats: any;
  selectedLastError: any;
  selectedOrganisation: any;

  enumFilterModel: any;
  textFilterModel: any;

  BUNDLE_PREFIX = 'documents.table.columnName.';

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    public documentService: DocumentService,
    public documentStateService: DocumentStateService) { }

  ngOnInit() {
    this.initSelected();
    this.initEnumFilterModel();
    this.initTextFilterModel();
  }

  initSelected() {
    this.storage.select('Document', null).subscribe(enumInfo => {
      this.statuses = enumInfo.documentStatus;
      this.documentTypes = enumInfo.documentType;
      this.ingoingFormats = enumInfo.ingoingDocumentFormat;
      this.lastErrors = enumInfo.lastError;
      this.selectedStatus = this.statuses[0];
      this.selectedDocumentType = this.documentTypes[0];
      this.selectedIngoingFormats = this.ingoingFormats[0];
      this.selectedLastError = this.lastErrors[0];
    });
    this.storage.select('organizations', null).subscribe(organisationsInfo => {
      this.organisations = organisationsInfo;
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
      },
      ingoingDocumentFormat: {
        value: this.selectedIngoingFormats,
        list: this.ingoingFormats
      }
    };
  }

  initTextFilterModel() {
    this.textFilterModel = {
      receiverIdentifier: null,
      senderName: null
    };
  }
}
