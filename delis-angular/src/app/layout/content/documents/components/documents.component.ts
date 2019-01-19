import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

import {routerTransition} from '../../../../router.animations';
import {DocumentsService} from '../services/documents.service';
import {DocumentsTestGuiStaticService} from "../services/documents.test-gui-static.service";
import {FilterProcessResult} from '../models/documents.model';
import {DateRangeModel} from '../../../../models/date.range.model';
import {LocaleService} from "../../../../service/locale.service";
import {environment} from "../../../../../environments/environment";
import {documentTypes} from '../models/documents.view.model';
import {ingoingFormats} from '../models/documents.view.model';
import {lastErrors} from '../models/documents.view.model';
import {statuses} from '../models/documents.view.model';
import {TableHeaderSortModel} from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import {PaginationService} from "../../../bs-component/components/pagination/pagination.service";
import {PaginationModel} from "../../../bs-component/components/pagination/pagination.model";
import {DocumentModel} from "../models/document.model";

const COLUMN_NAME_ORGANIZATION = 'documents.table.columnName.Organisation';
const COLUMN_NAME_RECEIVER = 'documents.table.columnName.Receiver';
const COLUMN_NAME_STATUS = 'documents.table.columnName.Status';
const COLUMN_NAME_LAST_ERROR = 'documents.table.columnName.LastError';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.columnName.DocumentType';
const COLUMN_NAME_INGOING_FORMAT = 'documents.table.columnName.IngoingFormat';
const COLUMN_NAME_RECEIVED = 'documents.table.columnName.Received';
const COLUMN_NAME_SENDER_NAME = 'documents.table.columnName.SenderName';

@Component({
    selector: 'app-documents',
    templateUrl: './documents.component.html',
    styleUrls: ['./documents.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsComponent implements OnInit {

    env = environment;

    selectedStatus: any;
    selectedLastError: any;
    selectedDocumentType: any;
    selectedIngoingFormat: any;

    textOrganisation: string;
    textReceiver: string;
    textSenderName: string;

    documents: DocumentModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    statuses = statuses;
    documentTypes = documentTypes;
    ingoingFormats = ingoingFormats;
    lastErrors = lastErrors;
    filter: FilterProcessResult;

    pagination: PaginationModel;

    constructor(
        private translate: TranslateService,
        private documentsService: DocumentsService,
        private documentsStaticService: DocumentsTestGuiStaticService,
        private locale: LocaleService,
        private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize !== 0) {
                this.loadPage(pag.currentPage, pag.pageSize);
                this.pagination = pag;
            } else {
                this.initDefaultValues();
                this.clearAllFilter();
                this.loadPage(pag.currentPage, pag.pageSize);
            }
        });
    }

    ngOnInit() {
        this.initProcess();
    }

    private initProcess() {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        if (this.env.production) {
            this.currentProdDocuments(1, 10);
        } else {
            this.currentDevDocuments(1, 10);
        }
        this.clearAllFilter();
    }

    private initDefaultValues() {

        this.selectedStatus = {status: 'ALL', selected: true};
        this.selectedLastError = {lastError: 'ALL', selected: true};
        this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
        this.selectedDocumentType = {documentType: 'ALL', selected: true};
        this.filter = new FilterProcessResult();
        if (this.tableHeaderSortModels.length == 0) {
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

    private currentProdDocuments(currentPage: number, sizeElement: number) {
        this.documentsService.getListDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.documents = data["items"];
            }, error => {
                localStorage.removeItem('isLoggedin');
            }
        );
    }

    private currentDevDocuments(currentPage: number, sizeElement: number) {
        this.documents = this.documentsStaticService.filterProcess({filter: this.filter});
        this.pagination.collectionSize = this.documents.length;
        this.pagination.currentPage = currentPage;
        this.pagination.pageSize = sizeElement;
        let startElement = (currentPage - 1) * sizeElement;
        this.documents = this.documents.slice(startElement, startElement + sizeElement);
    }

    private loadPage(page: number, pageSize: number) {
        if (this.env.production) {
            this.currentProdDocuments(page, pageSize);
        } else {
            this.currentDevDocuments(page, pageSize);
        }
    }

    loadTextOrganisation(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.organisation = null;
        } else {
            this.filter.organisation = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextReceiver(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiver = null;
        } else {
            this.filter.receiver = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadStatus() {
        if (this.selectedStatus === null) {
            this.selectedStatus = {status: 'ALL', selected: true};
        }
        this.pagination.currentPage = 1;
        this.filter.status = this.selectedStatus.status;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadLastErrors() {
        if (this.selectedLastError === null) {
            this.selectedLastError = {lastError: 'ALL', selected: true};
        }
        this.pagination.currentPage = 1;
        this.filter.lastError = this.selectedLastError.lastError;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadDocumentType() {
        if (this.selectedDocumentType === null) {
            this.selectedDocumentType = {documentType: 'ALL', selected: true};
        }
        this.pagination.currentPage = 1;
        this.filter.documentType = this.selectedDocumentType.documentType;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadIngoingFormat() {
        if (this.selectedIngoingFormat === null) {
            this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
        }
        this.pagination.currentPage = 1;
        this.filter.ingoingFormat = this.selectedIngoingFormat.ingoingFormat;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextSenderName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.senderName = null;
        } else {
            this.filter.senderName = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadReceivedDate(date: Date[]) {
        this.pagination.currentPage = 1;
        this.filter.dateReceived = new DateRangeModel();
        this.filter.dateReceived.dateStart = date[0];
        this.filter.dateReceived.dateEnd = date[1];
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
        if (countClick > 2) {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = 0;
        } else {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = countClick;
        }
        this.clearFilter(columnName);
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickReceiver = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVER).columnClick;
        this.filter.countClickStatus = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_STATUS).columnClick;
        this.filter.countClickLastError = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_LAST_ERROR).columnClick;
        this.filter.countClickDocumentType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DOCUMENT_TYPE).columnClick;
        this.filter.countClickIngoingFormat = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_INGOING_FORMAT).columnClick;
        this.filter.countClickReceived = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVED).columnClick;
        this.filter.countClickSenderName = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_SENDER_NAME).columnClick;
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickReceiver = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVER).columnClick;
        this.filter.countClickStatus = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_STATUS).columnClick;
        this.filter.countClickLastError = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_LAST_ERROR).columnClick;
        this.filter.countClickDocumentType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DOCUMENT_TYPE).columnClick;
        this.filter.countClickIngoingFormat = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_INGOING_FORMAT).columnClick;
        this.filter.countClickReceived = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVED).columnClick;
        this.filter.countClickSenderName = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_SENDER_NAME).columnClick;
    }
}
