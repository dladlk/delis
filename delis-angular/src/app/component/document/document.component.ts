import { Component, OnInit } from '@angular/core';
import {PaginationModel} from '../../model/system/pagination.model';
import {EnumInfoModel} from '../../model/system/enum-info.model';
import {TableHeaderSortModel} from '../../model/system/table-header-sort.model';
import {DocumentModel} from '../../model/content/document/document.model';
import {DocumentFilterModel} from '../../model/filter/document-filter.model';
import {SHOW_DATE_FORMAT} from '../../app.constants';
import {RefreshObservable} from '../../observable/refresh.observable';
import {Router} from '@angular/router';
import {LocalStorageService} from '../../service/system/local-storage.service';
import {TranslateService} from '@ngx-translate/core';
import {DocumentService} from '../../service/content/document.service';
import {LocaleService} from '../../service/system/locale.service';
import {ErrorService} from '../../service/system/error.service';
import {PaginationObservable} from '../../observable/pagination.observable';
import {DaterangeObservable} from '../../observable/daterange.observable';
import {AppStateService} from '../../service/system/app-state.service';
import {DateRangePickerModel} from '../../model/system/date-range-picker.model';
import {StateDocumentModel} from '../../model/state/state-document.model';
import {IAppPageState} from '../../model/state/app-page-state';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  private STATE_DOCUMENT = 'stateDocument';
  documents: DocumentModel[];
  tableHeaderSortModels: TableHeaderSortModel[] = [];
  statuses: EnumInfoModel[];
  documentTypes: EnumInfoModel[];
  ingoingFormats: EnumInfoModel[];
  lastErrors: EnumInfoModel[];
  organizations: string[];
  filter: DocumentFilterModel;

  pagination: PaginationModel;
  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  currentState: StateDocumentModel;

  constructor(
    private refreshObservable: RefreshObservable,
    private router: Router,
    private storage: LocalStorageService,
    private translate: TranslateService,
    private documentService: DocumentService,
    private locale: LocaleService,
    private errorService: ErrorService,
    private paginationObservable: PaginationObservable,
    private daterangeObservable: DaterangeObservable,
    private stateService: AppStateService) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.paginationObservable.listen().subscribe((pag: PaginationModel) => {
      if (location.href.endsWith('/document')) {
        if (pag === null || pag.collectionSize === 0) {
          this.clearAllFilter();
          this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
              this.router.navigate(['/document']));
        } else {
          if (pag.collectionSize <= pag.pageSize) {
            this.loadPage(1, this.pagination.pageSize);
          } else {
            this.loadPage(pag.currentPage, pag.pageSize);
          }
          this.pagination = pag;
        }
      }
    });
    this.daterangeObservable.listen().subscribe((dtRange: DateRangePickerModel) => {
      if (location.href.endsWith('/document')) {
        if (dtRange.startDate !== null && dtRange.endDate !== null) {
          this.filter.dateReceived = dtRange;
        } else {
          this.filter.dateReceived = null;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
      }
    });
    this.refreshObservable.listen().subscribe(() => {
      if (location.href.endsWith('/document')) {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/document']));
      }
    });
  }

  ngOnInit() {
    this.initProcess();
    this.initSelected();
  }

  initSelected() {
    this.storage.select('Document', null).subscribe(enumInfo => {
      this.statuses = enumInfo.documentStatus;
      this.documentTypes = enumInfo.documentType;
      this.ingoingFormats = enumInfo.ingoingDocumentFormat;
      this.lastErrors = enumInfo.lastError;
      if (this.currentState.selectedStatus === undefined) {
        this.currentState.selectedStatus = this.statuses[0];
      }
      if (this.currentState.selectedDocumentType === undefined) {
        this.currentState.selectedDocumentType = this.documentTypes[0];
      }
      if (this.currentState.selectedIngoingFormat === undefined) {
        this.currentState.selectedIngoingFormat = this.ingoingFormats[0];
      }
      if (this.currentState.selectedLastError === undefined) {
        this.currentState.selectedLastError = this.lastErrors[0];
      }
    });
    this.storage.select('organizations', null).subscribe(organizationsInfo => {
      this.organizations = organizationsInfo;
      if (this.currentState.selectedOrganization === null) {
        this.currentState.selectedOrganization = this.organizations[0];
      }
    });
  }

  protected initProcess() {
    this.initCurrentState();
    this.currentProdDocuments(this.pagination.currentPage, this.pagination.pageSize);
  }

  private initCurrentState() {
    let state = this.stateService.getState(this.STATE_DOCUMENT);
    if (state === null) {
      this.createOrUpdateCurrentState(null);
      state = this.stateService.getState(this.STATE_DOCUMENT);
    }
    this.currentState = state;
    this.filter = this.currentState.filter;
    this.pagination = this.currentState.pagination;
    this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
  }

  private createOrUpdateCurrentState(currentState: StateDocumentModel) {
    if (currentState === null) {
      let state: IAppPageState = {
        type: this.STATE_DOCUMENT,
        details: new StateDocumentModel()
      };
      this.stateService.addState(state);
    } else {
      let state: IAppPageState = {
        type: this.STATE_DOCUMENT,
        details: currentState
      };
      this.stateService.updateState(state);
    }
  }

  protected currentProdDocuments(currentPage: number, sizeElement: number) {
    this.documentService.getListDocuments(currentPage, sizeElement, this.filter).subscribe(
      (data: {}) => {
        this.pagination.collectionSize = data['collectionSize'];
        this.pagination.currentPage = data['currentPage'];
        this.pagination.pageSize = data['pageSize'];
        this.documents = data['items'];
        this.currentState.filter = this.filter;
        this.currentState.pagination = this.pagination;
        this.currentState.tableHeaderSortModels = this.tableHeaderSortModels;
        this.createOrUpdateCurrentState(this.currentState);
      }, error => {
        this.errorService.errorProcess(error);
      }
    );
  }

  protected loadPage(page: number, pageSize: number) {
    this.currentProdDocuments(page, pageSize);
  }

  loadOrganisations() {
    if (this.currentState.selectedOrganization === null) {
      this.currentState.selectedOrganization = this.organizations[0];
    }
    if (this.currentState.selectedOrganization === 'All' || this.currentState.selectedOrganization === 'Alle') {
      this.filter.organisation = null;
    } else {
      this.filter.organisation = this.currentState.selectedOrganization;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextReceiver(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.receiver = null;
    } else {
      this.filter.receiver = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadStatus() {
    if (this.currentState.selectedStatus === null) {
      this.currentState.selectedStatus = this.statuses[0];
    }
    this.filter.status = this.currentState.selectedStatus.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadLastErrors() {
    if (this.currentState.selectedLastError === null) {
      this.currentState.selectedLastError = this.lastErrors[0];
    }
    this.filter.lastError = this.currentState.selectedLastError.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadDocumentType() {
    if (this.currentState.selectedDocumentType === null) {
      this.currentState.selectedDocumentType = this.documentTypes[0];
    }
    this.filter.documentType = this.currentState.selectedDocumentType.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadIngoingFormat() {
    if (this.currentState.selectedIngoingFormat === null) {
      this.currentState.selectedIngoingFormat = this.ingoingFormats[0];
    }
    this.filter.ingoingFormat = this.currentState.selectedIngoingFormat.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextSenderName(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.senderName = null;
    } else {
      this.filter.senderName = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  clickFilter(target: string) {
    this.clickProcess(target);
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  clickProcess(columnName: string) {
    let countClick = this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick;
    countClick++;
    const columnEntity = columnName.split('.').reduce((first, last) => last);
    if (countClick === 1) {
      this.filter.sortBy = 'orderBy_' + columnEntity + '_Asc';
    }
    if (countClick === 2) {
      this.filter.sortBy = 'orderBy_' + columnEntity + '_Desc';
    }
    if (countClick > 2) {
      this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = 0;
      this.filter.sortBy = 'orderBy_Id_Desc';
    } else {
      this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = countClick;
    }
    this.clearFilter(columnName);
  }

  private clearFilter(columnName: string) {
    this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
  }

  protected clearAllFilter() {
    this.createOrUpdateCurrentState(null);
  }
}
