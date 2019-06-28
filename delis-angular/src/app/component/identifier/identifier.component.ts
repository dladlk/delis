import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {StateIdentifierModel} from '../../model/state/state-identifier.model';
import {EnumInfoModel} from '../../model/system/enum-info.model';
import {TableHeaderSortModel} from '../../model/system/table-header-sort.model';
import {IdentifierModel} from '../../model/content/identifier/identifier.model';
import {IdentifierFilterModel} from '../../model/filter/identifier-filter.model';
import {PaginationModel} from '../../model/system/pagination.model';
import {SHOW_DATE_FORMAT} from '../../app.constants';
import {RefreshObservable} from '../../observable/refresh.observable';
import {LocaleService} from '../../service/system/locale.service';
import {ErrorService} from '../../service/system/error.service';
import {PaginationObservable} from '../../observable/pagination.observable';
import {IdentifierService} from '../../service/content/identifier.service';
import {DaterangeObservable} from '../../observable/daterange.observable';
import {LocalStorageService} from '../../service/system/local-storage.service';
import {AppStateService} from '../../service/system/app-state.service';
import {DateRangePickerModel} from '../../model/system/date-range-picker.model';
import {IAppPageState} from '../../model/state/app-page-state';

@Component({
  selector: 'app-identifier',
  templateUrl: './identifier.component.html',
  styleUrls: ['./identifier.component.scss']
})
export class IdentifierComponent implements OnInit {

  private STATE_IDENTIFIER = 'stateIdentifier';

  pagination: PaginationModel;
  filter: IdentifierFilterModel;
  identifiers: IdentifierModel[];
  tableHeaderSortModels: TableHeaderSortModel[] = [];
  statusList: EnumInfoModel[];
  publishingStatusList: EnumInfoModel[];
  organizations: string[];

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
  show: boolean;

  currentState: StateIdentifierModel;

  constructor(
    private refreshObservable: RefreshObservable,
    private router: Router,
    private translate: TranslateService,
    private locale: LocaleService,
    private errorService: ErrorService,
    private paginationObservable: PaginationObservable,
    private identifierService: IdentifierService,
    private daterangeObservable: DaterangeObservable,
    private storage: LocalStorageService,
    private stateService: AppStateService) {
    this.show = false;
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.paginationObservable.listen().subscribe((pag: PaginationModel) => {
      if (location.href.endsWith('/identifier')) {
        if (pag === null || pag.collectionSize === 0) {
          this.clearAllFilter();
          this.refreshPage();
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
      if (location.href.endsWith('/identifier')) {
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
      if (location.href.endsWith('/identifier')) {
        this.refreshPage();
      }
    });
  }

  ngOnInit(): void {
    this.initProcess();
    this.initSelected();
  }

  private initProcess() {
    this.initCurrentState();
    this.currentProdIdentifiers(this.pagination.currentPage, this.pagination.pageSize);
  }

  initSelected() {
    this.organizations = JSON.parse(localStorage.getItem('organizations'));
    this.storage.select('Identifier', null).subscribe(enumInfo => {
      this.statusList = enumInfo.status;
      this.publishingStatusList = enumInfo.publishingStatus;
      if (this.currentState.selectedStatus === undefined) {
        this.currentState.selectedStatus = this.statusList[0];
      }
      if (this.currentState.selectedPublishingStatus === undefined) {
        this.currentState.selectedPublishingStatus = this.publishingStatusList[0];
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
    let state = this.stateService.getState(this.STATE_IDENTIFIER);
    if (state === null) {
      this.createOrUpdateCurrentState(null);
      state = this.stateService.getState(this.STATE_IDENTIFIER);
    }
    this.currentState = state;
    this.filter = this.currentState.filter;
    this.pagination = this.currentState.pagination;
    this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
  }

  private createOrUpdateCurrentState(currentState: StateIdentifierModel) {
    if (currentState === null) {
      let state: IAppPageState = {
        type: this.STATE_IDENTIFIER,
        details: new StateIdentifierModel()
      };
      this.stateService.addState(state);
    } else {
      let state: IAppPageState = {
        type: this.STATE_IDENTIFIER,
        details: currentState
      };
      this.stateService.updateState(state);
    }
  }

  clickFilter(target: string) {
    this.clickProcess(target);
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadStatus() {
    if (this.currentState.selectedStatus === null) {
      this.currentState.selectedStatus = this.statusList[0];
    }
    this.pagination.currentPage = 1;
    this.filter.status = this.currentState.selectedStatus.name;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadPublishingStatus() {
    if (this.currentState.selectedPublishingStatus === null) {
      this.currentState.selectedPublishingStatus = this.publishingStatusList[0];
    }
    this.pagination.currentPage = 1;
    this.filter.publishingStatus = this.currentState.selectedPublishingStatus.name;
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

  loadTextIdentifierGroup(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.identifierGroup = null;
    } else {
      this.filter.identifierGroup = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextType(text: string) {
    if (text.length === 0 || text == null) {
      this.filter.type = null;
    } else {
      this.filter.type = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextValue(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.value = null;
    } else {
      this.filter.value = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextUniqueValueType(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.uniqueValueType = null;
    } else {
      this.filter.uniqueValueType = text;
    }
    this.pagination.currentPage = 1;
    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
  }

  loadTextName(text: string) {
    if (text.length === 0 || text === null) {
      this.filter.name = null;
    } else {
      this.filter.name = text;
    }
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

  private loadPage(page: number, pageSize: number) {
    this.currentProdIdentifiers(page, pageSize);
  }

  private currentProdIdentifiers(currentPage: number, sizeElement: number) {
    this.identifierService.getListIdentifiers(currentPage, sizeElement, this.filter).subscribe(
      (data: {}) => {
        this.pagination.collectionSize = data['collectionSize'];
        this.pagination.currentPage = data['currentPage'];
        this.pagination.pageSize = data['pageSize'];
        this.identifiers = data['items'];
        this.show = true;
        this.currentState.filter = this.filter;
        this.currentState.pagination = this.pagination;
        this.currentState.tableHeaderSortModels = this.tableHeaderSortModels;
        this.createOrUpdateCurrentState(this.currentState);
      }, error => {
        this.errorService.errorProcess(error);
        this.show = false;
      }
    );
  }

  private clearFilter(columnName: string) {
    this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
  }

  private clearAllFilter() {
    this.createOrUpdateCurrentState(null);
  }

  private refreshPage() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/identifier']));
  }
}
