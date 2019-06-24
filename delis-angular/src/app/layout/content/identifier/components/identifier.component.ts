import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

import { routerTransition } from '../../../../router.animations';
import { PaginationModel } from '../../../bs-component/components/pagination/pagination.model';
import { IdentifierFilterProcessResult } from '../models/identifier.filter.process.result';
import { TableHeaderSortModel } from '../../../bs-component/components/table-header-sort/table.header.sort.model';
import { IdentifierModel } from '../models/identifier.model';
import { LocaleService } from '../../../../service/locale.service';
import { ErrorService } from '../../../../service/error.service';
import { PaginationService } from '../../../bs-component/components/pagination/pagination.service';
import { IdentifierService } from '../services/identifier.service';
import { DateRangeModel } from '../../../../models/date.range.model';
import { SHOW_DATE_FORMAT } from '../../../../app.constants';
import { DaterangeService } from '../../../bs-component/components/daterange/daterange.service';
import { EnumInfoModel } from '../../../../models/enum.info.model';
import { LocalStorageService } from '../../../../service/local.storage.service';
import { RefreshService } from '../../../../service/refresh.service';
import { StateIdentifierModel } from "../models/state.identifier.model";
import { AppStateService } from "../../../../service/app.state.service";

@Component({
    selector: 'app-identifiers',
    templateUrl: './identifier.component.html',
    styleUrls: ['./identifier.component.scss'],
    animations: [routerTransition()]
})
export class IdentifierComponent implements OnInit {

    clearableSelect = true;

    pagination: PaginationModel;
    filter: IdentifierFilterProcessResult;
    identifiers: IdentifierModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    statusList: EnumInfoModel[];
    publishingStatusList: EnumInfoModel[];
    organizations: string[];

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    show: boolean;

    currentState: StateIdentifierModel;

    constructor(
        private refreshService: RefreshService,
        private router: Router,
        private translate: TranslateService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private paginationService: PaginationService,
        private identifierService: IdentifierService,
        private dtService: DaterangeService,
        private storage: LocalStorageService,
        private stateService: AppStateService) {
        this.show = false;
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag === null || pag.collectionSize === 0) {
                this.clearAllFilter();
                this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                    this.router.navigate(['/identifiers']));
            } else {
                if (pag.collectionSize <= pag.pageSize) {
                    this.loadPage(1, this.pagination.pageSize);
                } else {
                    this.loadPage(pag.currentPage, pag.pageSize);
                }
                this.pagination = pag;
            }
        });
        this.dtService.listen().subscribe((dtRange: DateRangeModel) => {
            if (dtRange !== null) {
                this.filter.dateRange = dtRange;
            } else {
                this.filter.dateRange = null;
            }
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
        });
        this.refreshService.listen().subscribe(() => {
            this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                this.router.navigate(['/identifiers']));
        });
    }

    ngOnInit(): void {
        this.initProcess();
        this.initSelected();
    }

    private initProcess() {
        this.initDefaultValues();
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

    private initDefaultValues() {
        this.currentState = this.stateService.getFilterIdentifierState();
        this.filter = this.currentState.filter;
        this.pagination = this.currentState.pagination;
        this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
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
                this.stateService.setFilterIdentifierState(this.currentState);
            }, error => {
                this.errorService.errorProcess(error);
                this.show = false;
            }
        );
    }

    private clearAllFilter() {
        this.stateService.clearFilterIdentifierState();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
    }
}
