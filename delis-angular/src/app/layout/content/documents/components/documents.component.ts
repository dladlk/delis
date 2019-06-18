import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from '../../../../router.animations';
import { DocumentsService } from '../services/documents.service';
import { FilterProcessResult } from '../models/filter.process.result';
import { DateRangeModel } from '../../../../models/date.range.model';
import { LocaleService } from "../../../../service/locale.service";
import { TableHeaderSortModel } from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import { PaginationService } from "../../../bs-component/components/pagination/pagination.service";
import { PaginationModel } from "../../../bs-component/components/pagination/pagination.model";
import { DocumentModel } from "../models/document.model";
import { ErrorService } from "../../../../service/error.service";
import { SHOW_DATE_FORMAT } from 'src/app/app.constants';
import { DaterangeService } from "../../../bs-component/components/daterange/daterange.service";
import { DaterangeShowService } from "../../../bs-component/components/daterange/daterange.show.service";
import {EnumInfoModel} from "../../../../models/enum.info.model";

const COLUMN_NAME_ORGANIZATION = 'documents.table.columnName.organisation';
const COLUMN_NAME_RECEIVER = 'documents.table.columnName.receiverIdentifier';
const COLUMN_NAME_STATUS = 'documents.table.columnName.documentStatus';
const COLUMN_NAME_LAST_ERROR = 'documents.table.columnName.lastError';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.columnName.documentType';
const COLUMN_NAME_INGOING_FORMAT = 'documents.table.columnName.ingoingDocumentFormat';
const COLUMN_NAME_RECEIVED = 'documents.table.columnName.createTime';
const COLUMN_NAME_SENDER_NAME = 'documents.table.columnName.senderName';

@Component({
    selector: 'app-documents',
    templateUrl: './documents.component.html',
    styleUrls: ['./documents.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsComponent implements OnInit {

    clearableSelect = true;

    selectedStatus: EnumInfoModel = new EnumInfoModel();
    selectedLastError: EnumInfoModel = new EnumInfoModel();
    selectedDocumentType: EnumInfoModel = new EnumInfoModel();
    selectedIngoingFormat: EnumInfoModel = new EnumInfoModel();
    selectedOrganization: any;

    textReceiver: string;
    textSenderName: string;
    textPlaceholderReceivedDate: string;

    documents: DocumentModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    statuses: EnumInfoModel[];
    documentTypes: EnumInfoModel[];
    ingoingFormats: EnumInfoModel[];
    lastErrors: EnumInfoModel[];
    organizations: [];
    filter: FilterProcessResult;

    pagination: PaginationModel;
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    show: boolean;

    constructor(
        private translate: TranslateService,
        private documentsService: DocumentsService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private paginationService: PaginationService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService) {
        this.show = false;
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize !== 0) {
                if (pag.collectionSize <= pag.pageSize) {
                    this.loadPage(1, this.pagination.pageSize);
                } else {
                    this.loadPage(pag.currentPage, pag.pageSize);
                }
            } else {
                this.initDefaultValues();
                this.clearAllFilter();
                this.loadPage(pag.currentPage, pag.pageSize);
            }
            this.pagination = pag;
        });
        this.dtService.listen().subscribe((dtRange: DateRangeModel) => {
            if (dtRange.dateStart !== null && dtRange.dateEnd !== null) {
                this.filter.dateReceived = dtRange;
            } else {
                this.filter.dateReceived = null;
            }
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
        });
        this.dtShowService.listen().subscribe((show: boolean) => {
            this.filter.dateReceived = null;
            this.loadPage(1, this.pagination.pageSize);
        });
    }

    ngOnInit() {
        this.initProcess();
        this.initSelected();
    }

    initSelected() {
        let select = JSON.parse(localStorage.getItem('Document'));
        this.statuses = select.documentStatus;
        this.documentTypes = select.documentType;
        this.ingoingFormats = select.ingoingDocumentFormat;
        this.lastErrors = select.lastError;
        select = JSON.parse(localStorage.getItem('organizations'));
        this.organizations = select;
    }

    protected initProcess() {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        this.currentProdDocuments(1, 10);
        this.clearAllFilter();
    }

    protected initDefaultValues() {
        this.selectedStatus = new EnumInfoModel();
        this.selectedDocumentType = new EnumInfoModel();
        this.selectedIngoingFormat = new EnumInfoModel();
        this.selectedLastError = new EnumInfoModel();
        this.selectedOrganization = 'ALL';
        this.textPlaceholderReceivedDate = 'Received Date';
        this.filter = new FilterProcessResult();
        if (this.tableHeaderSortModels.length === 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_RECEIVER, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_STATUS, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_LAST_ERROR, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_DOCUMENT_TYPE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_INGOING_FORMAT, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_RECEIVED, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_SENDER_NAME, columnClick: 0
                }
            );
        }
    }

    protected currentProdDocuments(currentPage: number, sizeElement: number) {
        this.documentsService.getListDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.documents = data["items"];
                this.show = true;
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
        if (this.selectedOrganization === null) {
            this.selectedOrganization = 'ALL';
        }
        this.filter.organisation = this.selectedOrganization;
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
        this.filter.status = this.selectedStatus.name;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadLastErrors() {
        this.filter.lastError = this.selectedLastError.name;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadDocumentType() {
        this.filter.documentType = this.selectedDocumentType.name;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadIngoingFormat() {
        this.filter.ingoingFormat = this.selectedIngoingFormat.name;
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
        let columnEntity = columnName.split('.').reduce((first, last) => last);
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
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.selectedStatus = new EnumInfoModel();
        this.selectedDocumentType = new EnumInfoModel();
        this.selectedIngoingFormat = new EnumInfoModel();
        this.selectedLastError = new EnumInfoModel();
        this.selectedOrganization = 'ALL';
        this.textReceiver = '';
        this.textSenderName = '';
        this.filter.dateReceived = null;
        this.filter.sortBy = 'orderBy_Id_Desc';
    }
}
