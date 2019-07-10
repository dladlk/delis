import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { Router } from '@angular/router';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';

import {DOCUMENT_PATH, SHOW_DATE_FORMAT} from '../../../app.constants';

import { DocumentFilterModel } from '../../../model/filter/document-filter.model';
import { DocumentDataSource } from './document-data-source';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { DocumentService } from '../../../service/content/document.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { RefreshObservable} from '../../../observable/refresh.observable';
import { Range } from '../../system/date-range/model/model';
import { HideColumnModel } from "../../../model/content/hide-column.model";
import {DocumentStateService} from "../../../service/state/document-state.service";

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

  BUNDLE_PREFIX = 'documents.table.columnName.';

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    private documentService: DocumentService,
    private documentStateService: DocumentStateService) { }

  ngOnInit() {
    this.initSelected();
    this.initEnumFilterModel();
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
    }
  }
}
