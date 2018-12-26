import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from '../../../router.animations';
import { DocumentsService } from '../services/documents.service';
import { DocumentsStaticService } from "../services/documents.static.service";
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { DateRangeModel } from '../../../models/date.range.model';
import { LocaleService } from "../../../service/locale.service";
import { environment } from "../../../../environments/environment";
import { documentTypes } from '../models/documents.view.model';
import { ingoingFormats } from '../models/documents.view.model';
import { lastErrors } from '../models/documents.view.model';
import { statuses } from '../models/documents.view.model';
import { TableHeaderSortModel } from "../../bs-component/components/table-header-sort/table.header.sort.model";
import { PaginationService } from "../../bs-component/components/pagination/pagination.service";
import { PaginationModel } from "../../bs-component/components/pagination/pagination.model";

const COLUMN_NAME_ORGANIZATION = 'documents.table.columnName.Organisation';
const COLUMN_NAME_RECEIVER = 'documents.table.columnName.Receiver';
const COLUMN_NAME_STATUS = 'documents.table.columnName.Status';
const COLUMN_NAME_LAST_ERROR = 'documents.table.columnName.LastError';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.columnName.DocumentType';
const COLUMN_NAME_INGOING_FORMAT = 'documents.table.columnName.IngoingFormat';
const COLUMN_NAME_RECEIVED = 'documents.table.columnName.Received';
const COLUMN_NAME_ISSUED = 'documents.table.columnName.Issued';
const COLUMN_NAME_SENDER_NAME = 'documents.table.columnName.SenderName';
const COLUMN_NAME_RECEIVER_NAME = 'documents.table.columnName.ReceiverName';

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
    textReceiverName: string;

    documents: DocumentsModel[];
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
        private documentsStaticService: DocumentsStaticService,
        private locale: LocaleService,
        private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize !== 0) {
                this.loadPage(pag.currentPage, pag.pageSize);
                this.pagination = pag;
            } else {
                this.initProcess();
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
                    columnName: COLUMN_NAME_ISSUED, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_SENDER_NAME, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_RECEIVER_NAME, columnClick: 0
                }
            );
        }
    }

    private currentProdDocuments(currentPage: number, sizeElement: number) {
        this.documentsService.getAnyDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.documents = data["items"];
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

    loadTextReceiverName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiverName = null;
        } else {
            this.filter.receiverName = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadReceivedDate(date: Date[]) {
        this.pagination.currentPage = 1;
        this.filter.dateReceived = new DateRangeModel(date[0], date[1]);
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadIssuedDate(date: Date[]) {
        this.pagination.currentPage = 1;
        this.filter.dateIssued = new DateRangeModel(date[0], date[1]);
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    clickFilter(target: string) {
        if (target === COLUMN_NAME_ORGANIZATION) {
            this.clickOrganisation();
        }
        if (target === COLUMN_NAME_RECEIVER) {
            this.clickReceiver();
        }
        if (target === COLUMN_NAME_STATUS) {
            this.clickStatus();
        }
        if (target === COLUMN_NAME_LAST_ERROR) {
            this.clickLastError();
        }
        if (target === COLUMN_NAME_DOCUMENT_TYPE) {
            this.clickDocumentType();
        }
        if (target === COLUMN_NAME_INGOING_FORMAT) {
            this.clickIngoingFormat();
        }
        if (target === COLUMN_NAME_RECEIVED) {
            this.clickReceived();
        }
        if (target === COLUMN_NAME_ISSUED) {
            this.clickIssued();
        }
        if (target === COLUMN_NAME_SENDER_NAME) {
            this.clickSenderName();
        }
        if (target === COLUMN_NAME_RECEIVER_NAME) {
            this.clickReceiverName();
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    private clickOrganisation() {
        this.filter.countClickOrganisation++;
        if (this.filter.countClickOrganisation > 2) {
            this.filter.countClickOrganisation = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick = this.filter.countClickOrganisation;
    }

    private clickReceiver() {
        this.filter.countClickReceiver++;
        if (this.filter.countClickReceiver > 2) {
            this.filter.countClickReceiver = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVER).columnClick = this.filter.countClickReceiver;
    }

    private clickStatus() {
        this.filter.countClickStatus++;
        if (this.filter.countClickStatus > 2) {
            this.filter.countClickStatus = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_STATUS).columnClick = this.filter.countClickStatus;
    }

    private clickLastError() {
        this.filter.countClickLastError++;
        if (this.filter.countClickLastError > 2) {
            this.filter.countClickLastError = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_LAST_ERROR).columnClick = this.filter.countClickLastError;
    }

    private clickDocumentType() {
        this.filter.countClickDocumentType++;
        if (this.filter.countClickDocumentType > 2) {
            this.filter.countClickDocumentType = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DOCUMENT_TYPE).columnClick = this.filter.countClickDocumentType;
    }

    private clickIngoingFormat() {
        this.filter.countClickIngoingFormat++;
        if (this.filter.countClickIngoingFormat > 2) {
            this.filter.countClickIngoingFormat = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_INGOING_FORMAT).columnClick = this.filter.countClickIngoingFormat;
    }

    private clickReceived() {
        this.filter.countClickReceived++;
        if (this.filter.countClickReceived > 2) {
            this.filter.countClickReceived = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVED).columnClick = this.filter.countClickReceived;
    }

    private clickIssued() {
        this.filter.countClickIssued++;
        if (this.filter.countClickIssued > 2) {
            this.filter.countClickIssued = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ISSUED).columnClick = this.filter.countClickIssued;
    }

    private clickSenderName() {
        this.filter.countClickSenderName++;
        if (this.filter.countClickSenderName > 2) {
            this.filter.countClickSenderName = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_SENDER_NAME).columnClick = this.filter.countClickSenderName;
    }

    private clickReceiverName() {
        this.filter.countClickReceiverName++;
        if (this.filter.countClickReceiverName > 2) {
            this.filter.countClickReceiverName = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_RECEIVER_NAME).columnClick = this.filter.countClickReceiverName;
    }
}
