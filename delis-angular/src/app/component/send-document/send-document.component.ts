import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {StateSendDocumentModel} from '../../model/state/state-send-document.model';
import {EnumInfoModel} from '../../model/system/enum-info.model';
import {SHOW_DATE_FORMAT} from '../../app.constants';
import {AppStateService} from '../../service/system/app-state.service';
import {RefreshObservable} from '../../observable/refresh.observable';
import {LocalStorageService} from '../../service/system/local-storage.service';
import {LocaleService} from '../../service/system/locale.service';
import {ErrorService} from '../../service/system/error.service';
import {PaginationObservable} from '../../observable/pagination.observable';
import {DaterangeObservable} from '../../observable/daterange.observable';
import {SendDocumentService} from '../../service/content/send-document.service';
import {PaginationModel} from '../../model/system/pagination.model';
import {TableHeaderSortModel} from '../../model/system/table-header-sort.model';
import {SendDocumentFilterModel} from '../../model/filter/send-document-filter.model';
import {SendDocumentModel} from '../../model/content/send-document/send-document.model';
import {IAppPageState} from '../../model/state/app-page-state';
import {DateRangePickerModel} from '../../model/system/date-range-picker.model';

@Component({
  selector: 'app-send-document',
  templateUrl: './send-document.component.html',
  styleUrls: ['./send-document.component.scss']
})
export class SendDocumentComponent implements OnInit {

  private STATE_DOCUMENT_SEND = 'stateDocumentSend';
  currentState: StateSendDocumentModel;
  organizations: string[] = [];
  statuses: EnumInfoModel[] = [];
  documentTypes: EnumInfoModel[] = [];
  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  pagination: PaginationModel;
  tableHeaderSortModels: TableHeaderSortModel[] = [];
  filter: SendDocumentFilterModel;
  sendDocuments: SendDocumentModel[] = [];

  constructor(
    private router: Router,
    private translate: TranslateService,
    private storage: LocalStorageService,
    private locale: LocaleService,
    private errorService: ErrorService,
    private stateService: AppStateService,
    private refreshObservable: RefreshObservable,
    private paginationObservable: PaginationObservable,
    private daterangeObservable: DaterangeObservable,
    private sendDocumentService: SendDocumentService) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.paginationObservable.listen().subscribe((pag: PaginationModel) => {
      if (location.href.endsWith('/send-document')) {
        if (pag === null || pag.collectionSize === 0) {
          this.clearAllFilter();
          this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
              this.router.navigate(['/send-document']));
        } else {
          this.pagination = pag;
          if (pag.collectionSize <= pag.pageSize) {
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
          } else {
            this.loadPage(pag.currentPage, pag.pageSize);
          }
        }
      }
    });
    this.daterangeObservable.listen().subscribe((dtRange: DateRangePickerModel) => {
      if (location.href.endsWith('/send-document')) {
        if (dtRange.startDate !== null && dtRange.endDate !== null) {
          this.filter.dateRange = dtRange;
        } else {
          this.filter.dateRange = null;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
      }
    });
    this.refreshObservable.listen().subscribe(() => {
      if (location.href.endsWith('/send-document')) {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/send-document']));
      }
    });
  }

  ngOnInit(): void {
    this.initProcess();
    this.initSelected();
  }

  private initProcess() {
    this.initCurrentState();
    this.currentProdSendDocuments(this.pagination.currentPage, this.pagination.pageSize);
  }

  initSelected() {
    this.storage.select('SendDocument', null).subscribe(enumInfo => {
      this.statuses = enumInfo.documentStatus;
      this.documentTypes = enumInfo.documentType;
      if (this.currentState.selectedStatus === undefined) {
        this.currentState.selectedStatus = this.statuses[0];
      }
      if (this.currentState.selectedDocumentType === undefined) {
        this.currentState.selectedDocumentType = this.documentTypes[0];
      }
    });
    this.storage.select('organizations', null).subscribe(organizationsInfo => {
      this.organizations = organizationsInfo;
      if (this.currentState.selectedOrganization === undefined) {
        this.currentState.selectedOrganization = this.organizations[0];
      }
    });
  }

  private initCurrentState() {
    let state = this.stateService.getState(this.STATE_DOCUMENT_SEND);
    if (state === null) {
      this.createOrUpdateCurrentState(null);
      state = this.stateService.getState(this.STATE_DOCUMENT_SEND);
    }
    this.currentState = state;
    this.filter = this.currentState.filter;
    this.pagination = this.currentState.pagination;
    this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
  }

  private createOrUpdateCurrentState(currentState: StateSendDocumentModel) {
    if (currentState === null) {
      let state: IAppPageState = {
        type: this.STATE_DOCUMENT_SEND,
        details: new StateSendDocumentModel()
      };
      this.stateService.addState(state);
    } else {
      let state: IAppPageState = {
        type: this.STATE_DOCUMENT_SEND,
        details: currentState
      };
      this.stateService.updateState(state);
    }
  }

  private loadPage(page: number, pageSize: number) {
    this.currentProdSendDocuments(page, pageSize);
  }

  loadDocumentType() {
    if (this.currentState.selectedDocumentType === null) {
      this.currentState.selectedDocumentType = this.documentTypes[0];
    }
    this.filter.documentType = this.currentState.selectedDocumentType.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadStatus() {
    if (this.currentState.selectedStatus === null) {
      this.currentState.selectedStatus = this.statuses[0];
    }
    this.filter.documentStatus = this.currentState.selectedStatus.name;
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextSender(text: string) {
    if (text.length === 0) {
      this.filter.senderIdRaw = null;
    } else {
      this.filter.senderIdRaw = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextReceiver(text: string) {
    if (text.length === 0) {
      this.filter.receiverIdRaw = null;
    } else {
      this.filter.receiverIdRaw = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  clickFilter(target: string) {
    this.clickProcess(target);
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
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

  private currentProdSendDocuments(currentPage: number, sizeElement: number) {
    this.sendDocumentService.getListSendDocuments(currentPage, sizeElement, this.filter).subscribe(
      (data: {}) => {
        this.pagination.collectionSize = data['collectionSize'];
        this.pagination.currentPage = data['currentPage'];
        this.pagination.pageSize = data['pageSize'];
        this.sendDocuments = data['items'];
        this.currentState.filter = this.filter;
        this.currentState.pagination = this.pagination;
        this.currentState.tableHeaderSortModels = this.tableHeaderSortModels;
        this.createOrUpdateCurrentState(this.currentState);
      }, error => {
        this.errorService.errorProcess(error);
      }
    );
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

  private clearAllFilter() {
    this.createOrUpdateCurrentState(null);
  }

  private clearFilter(columnName: string) {
    this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
  }
}
