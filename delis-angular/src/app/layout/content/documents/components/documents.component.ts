import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

import { routerTransition } from '../../../../router.animations';
import { DocumentsService } from '../services/documents.service';
import { FilterProcessResult } from '../models/filter.process.result';
import { LocaleService } from '../../../../service/locale.service';
import { TableHeaderSortModel } from '../../../bs-component/components/table-header-sort/table.header.sort.model';
import { PaginationService } from '../../../bs-component/components/pagination/pagination.service';
import { PaginationModel } from '../../../bs-component/components/pagination/pagination.model';
import { DocumentModel } from '../models/document.model';
import { ErrorService } from '../../../../service/error.service';
import { SHOW_DATE_FORMAT } from 'src/app/app.constants';
import { DaterangeService } from '../../../bs-component/components/daterange/daterange.service';
import { EnumInfoModel } from '../../../../models/enum.info.model';
import { LocalStorageService } from '../../../../service/local.storage.service';
import { RefreshService } from '../../../../service/refresh.service';
import { StateDocumentsModel } from "../models/state.documents.model";
import { AppStateService } from "../../../../service/app.state.service";
import { DateRangePicker } from "../../../bs-component/components/daterange/date.range.picker";

@Component({
    selector: 'app-documents',
    templateUrl: './documents.component.html',
    styleUrls: ['./documents.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsComponent implements OnInit {

    clearableSelect = true;

    documents: DocumentModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    statuses: EnumInfoModel[];
    documentTypes: EnumInfoModel[];
    ingoingFormats: EnumInfoModel[];
    lastErrors: EnumInfoModel[];
    organizations: string[];
    filter: FilterProcessResult;

    pagination: PaginationModel;
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    show: boolean;

    currentState: StateDocumentsModel;

    constructor(
        private refreshService: RefreshService,
        private router: Router,
        private storage: LocalStorageService,
        private translate: TranslateService,
        private documentsService: DocumentsService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private paginationService: PaginationService,
        private dtService: DaterangeService,
        private stateService: AppStateService) {
        this.show = false;
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag === null || pag.collectionSize === 0) {
                this.clearAllFilter();
                this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                    this.router.navigate(['/documents']));
            } else {
                if (pag.collectionSize <= pag.pageSize) {
                    this.loadPage(1, this.pagination.pageSize);
                } else {
                    this.loadPage(pag.currentPage, pag.pageSize);
                }
                this.pagination = pag;
            }
        });
        this.dtService.listen().subscribe((dtRange: DateRangePicker) => {
            if (dtRange.startDate !== null && dtRange.endDate !== null) {
                this.filter.dateReceived = dtRange;
            } else {
                this.filter.dateReceived = null;
            }
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
        });
        this.refreshService.listen().subscribe(() => {
            this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                this.router.navigate(['/documents']));
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
        this.initDefaultValues();
        this.currentProdDocuments(this.pagination.currentPage, this.pagination.pageSize);
    }

    protected initDefaultValues() {
        this.currentState = this.stateService.getFilterDocumentState();
        this.filter = this.currentState.filter;
        this.pagination = this.currentState.pagination;
        this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
    }

    protected currentProdDocuments(currentPage: number, sizeElement: number) {
        this.documentsService.getListDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data['collectionSize'];
                this.pagination.currentPage = data['currentPage'];
                this.pagination.pageSize = data['pageSize'];
                this.documents = data['items'];
                this.show = true;
                this.currentState.filter = this.filter;
                this.currentState.pagination = this.pagination;
                this.currentState.tableHeaderSortModels = this.tableHeaderSortModels;
                this.stateService.setFilterDocumentState(this.currentState);
            }, error => {
                this.errorService.errorProcess(error);
                this.show = false;
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
        this.stateService.clearFilterDocumentState();
    }
}
