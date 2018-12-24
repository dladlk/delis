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
                    columnName: 'Organisation', columnClick: 0
                },
                {
                    columnName: 'Receiver', columnClick: 0
                },
                {
                    columnName: 'Status', columnClick: 0
                },
                {
                    columnName: 'Last Error', columnClick: 0
                },
                {
                    columnName: 'Document Type', columnClick: 0
                },
                {
                    columnName: 'Ingoing Format', columnClick: 0
                },
                {
                    columnName: 'Received', columnClick: 0
                },
                {
                    columnName: 'Issued', columnClick: 0
                },
                {
                    columnName: 'Sender Name', columnClick: 0
                },
                {
                    columnName: 'Receiver Name', columnClick: 0
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
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextReceiver(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiver = null;
        } else {
            this.filter.receiver = text;
        }
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadStatus() {
        if (this.selectedStatus === null) {
            this.selectedStatus = {status: 'ALL', selected: true};
        }
        this.filter.status = this.selectedStatus.status;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadLastErrors() {
        if (this.selectedLastError === null) {
            this.selectedLastError = {lastError: 'ALL', selected: true};
        }
        this.filter.lastError = this.selectedLastError.lastError;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadDocumentType() {
        if (this.selectedDocumentType === null) {
            this.selectedDocumentType = {documentType: 'ALL', selected: true};
        }
        this.filter.documentType = this.selectedDocumentType.documentType;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadIngoingFormat() {
        if (this.selectedIngoingFormat === null) {
            this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
        }
        this.filter.ingoingFormat = this.selectedIngoingFormat.ingoingFormat;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextSenderName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.senderName = null;
        } else {
            this.filter.senderName = text;
        }
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextReceiverName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiverName = null;
        } else {
            this.filter.receiverName = text;
        }
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadReceivedDate(date: Date[]) {
        this.filter.dateReceived = new DateRangeModel(date[0], date[1]);
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadIssuedDate(date: Date[]) {
        this.filter.dateIssued = new DateRangeModel(date[0], date[1]);
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    clickFilter(target: string) {
        if (target === 'Organisation') {
            this.clickOrganisation();
        }
        if (target === 'Receiver') {
            this.clickReceiver();
        }
        if (target === 'Status') {
            this.clickStatus();
        }
        if (target === 'Last Error') {
            this.clickLastError();
        }
        if (target === 'Document Type') {
            this.clickDocumentType();
        }
        if (target === 'Ingoing Format') {
            this.clickIngoingFormat();
        }
        if (target === 'Received') {
            this.clickReceived();
        }
        if (target === 'Issued') {
            this.clickIssued();
        }
        if (target === 'Sender Name') {
            this.clickSenderName();
        }
        if (target === 'Receiver Name') {
            this.clickReceiverName();
        }
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    private clickOrganisation() {
        this.filter.countClickOrganisation++;
        if (this.filter.countClickOrganisation > 2) {
            this.filter.countClickOrganisation = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Organisation').columnClick = this.filter.countClickOrganisation;
    }

    private clickReceiver() {
        this.filter.countClickReceiver++;
        if (this.filter.countClickReceiver > 2) {
            this.filter.countClickReceiver = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Receiver').columnClick = this.filter.countClickReceiver;
    }

    private clickStatus() {
        this.filter.countClickStatus++;
        if (this.filter.countClickStatus > 2) {
            this.filter.countClickStatus = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Status').columnClick = this.filter.countClickStatus;
    }

    private clickLastError() {
        this.filter.countClickLastError++;
        if (this.filter.countClickLastError > 2) {
            this.filter.countClickLastError = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Last Error').columnClick = this.filter.countClickLastError;
    }

    private clickDocumentType() {
        this.filter.countClickDocumentType++;
        if (this.filter.countClickDocumentType > 2) {
            this.filter.countClickDocumentType = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Document Type').columnClick = this.filter.countClickDocumentType;
    }

    private clickIngoingFormat() {
        this.filter.countClickIngoingFormat++;
        if (this.filter.countClickIngoingFormat > 2) {
            this.filter.countClickIngoingFormat = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Ingoing Format').columnClick = this.filter.countClickIngoingFormat;
    }

    private clickReceived() {
        this.filter.countClickReceived++;
        if (this.filter.countClickReceived > 2) {
            this.filter.countClickReceived = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Received').columnClick = this.filter.countClickReceived;
    }

    private clickIssued() {
        this.filter.countClickIssued++;
        if (this.filter.countClickIssued > 2) {
            this.filter.countClickIssued = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Issued').columnClick = this.filter.countClickIssued;
    }

    private clickSenderName() {
        this.filter.countClickSenderName++;
        if (this.filter.countClickSenderName > 2) {
            this.filter.countClickSenderName = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Sender Name').columnClick = this.filter.countClickSenderName;
    }

    private clickReceiverName() {
        this.filter.countClickReceiverName++;
        if (this.filter.countClickReceiverName > 2) {
            this.filter.countClickReceiverName = 0;
        }
        this.tableHeaderSortModels.find(k => k.columnName === 'Receiver Name').columnClick = this.filter.countClickReceiverName;
    }
}
