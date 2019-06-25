import { Component, OnInit} from '@angular/core';
import { Router} from '@angular/router';
import { TranslateService} from '@ngx-translate/core';

import { routerTransition } from '../../../../router.animations';
import { PaginationModel } from '../../../bs-component/components/pagination/pagination.model';
import { TableHeaderSortModel } from '../../../bs-component/components/table-header-sort/table.header.sort.model';
import { SendDocumentsModel } from '../models/send.documents.model';
import { SendDocumentsFilterProcessResult } from '../models/send.documents.filter.process.result';
import { SHOW_DATE_FORMAT } from '../../../../app.constants';
import { LocaleService } from '../../../../service/locale.service';
import { ErrorService } from '../../../../service/error.service';
import { PaginationService } from '../../../bs-component/components/pagination/pagination.service';
import { DaterangeService } from '../../../bs-component/components/daterange/daterange.service';
import { SendDocumentsService } from '../service/send.documents.service';
import { EnumInfoModel } from '../../../../models/enum.info.model';
import { LocalStorageService } from '../../../../service/local.storage.service';
import { RefreshService } from '../../../../service/refresh.service';
import { AppStateService } from "../../../../service/app.state.service";
import { StateSendDocumentsModel } from "../models/state.send.documents.model";
import { DateRangePicker } from "../../../bs-component/components/daterange/date.range.picker";

@Component({
    selector: 'app-send-document',
    templateUrl: './send.document.component.html',
    styleUrls: ['./send.document.component.scss'],
    animations: [routerTransition()]
})
export class SendDocumentsComponent implements OnInit {

    clearableSelect = true;

    pagination: PaginationModel;
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    filter: SendDocumentsFilterProcessResult;
    sendDocuments: SendDocumentsModel[] = [];

    organizations: string[] = [];
    statuses: EnumInfoModel[] = [];
    documentTypes: EnumInfoModel[] = [];

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    show: boolean;

    currentState: StateSendDocumentsModel;

    constructor(
        private refreshService: RefreshService,
        private router: Router,
        private storage: LocalStorageService,
        private translate: TranslateService,
        private sendDocumentsService: SendDocumentsService,
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
                    this.router.navigate(['/send-documents']));
            } else {
                this.pagination = pag;
                if (pag.collectionSize <= pag.pageSize) {
                    this.pagination.currentPage = 1;
                    this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
                } else {
                    this.loadPage(pag.currentPage, pag.pageSize);
                }
            }
        });
        this.dtService.listen().subscribe((dtRange: DateRangePicker) => {
            if (dtRange.startDate !== null && dtRange.endDate !== null) {
                this.filter.dateRange = dtRange;
            } else {
                this.filter.dateRange = null;
            }
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
        });
        this.refreshService.listen().subscribe(() => {
            this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                this.router.navigate(['/send-documents']));
        });
    }

    ngOnInit(): void {
        this.initProcess();
        this.initSelected();
    }

    private initProcess() {
        this.initDefaultValues();
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

    private initDefaultValues() {
        this.currentState = this.stateService.getFilterDocumentSendState();
        this.filter = this.currentState.filter;
        this.pagination = this.currentState.pagination;
        this.tableHeaderSortModels = this.currentState.tableHeaderSortModels;
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
        this.sendDocumentsService.getListSendDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data['collectionSize'];
                this.pagination.currentPage = data['currentPage'];
                this.pagination.pageSize = data['pageSize'];
                this.sendDocuments = data['items'];
                this.show = true;
                this.currentState.filter = this.filter;
                this.currentState.pagination = this.pagination;
                this.currentState.tableHeaderSortModels = this.tableHeaderSortModels;
                this.stateService.setFilterDocumentSendState(this.currentState);
            }, error => {
                this.errorService.errorProcess(error);
                this.show = false;
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
        this.stateService.clearFilterDocumentSendState();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
    }
}
