import { Component, OnInit } from '@angular/core';

import { routerTransition } from '../../../router.animations';
import { DocumentsService } from '../services/documents.service';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { DateRangeModel } from '../../../models/date.range.model';
import { PaginationModel } from '../../../models/pagination.model';

@Component({
    selector: 'app-documents',
    templateUrl: './documents.component.html',
    styleUrls: ['./documents.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsComponent implements OnInit {

    selectedStatus: any;
    selectedLastError: any;
    selectedDocumentType: any;
    selectedIngoingFormat: any;
    selectedPageSize: any;
    textOrganisation: string;
    textReceiver: string;
    textSenderName: string;
    textReceiverName: string;

    documents: DocumentsModel[];
    filter: FilterProcessResult;
    pagination: PaginationModel;

    countClickOrganisation: number;
    countClickReceiver: number;
    countClickStatus: number;
    countClickLastError: number;
    countClickDocumentType: number;
    countClickIngoingFormat: number;
    countClickReceived: number;
    countClickIssued: number;
    countClickSenderName: number;
    countClickReceiverName: number;

    pageSizes = [
        {pageSize: 5},
        {pageSize: 10},
        {pageSize: 20},
        {pageSize: 50},
        {pageSize: 100}
    ];

    dateReceiveds = [
        {dateReceived: 'LAST HOURS', selected: true},
        {dateReceived: 'LAST DAY', selected: false},
        {dateReceived: 'LAST WEEK', selected: false},
        {dateReceived: 'LAST MONTH', selected: false},
        {dateReceived: 'LAST YEAR', selected: false},
        {dateReceived: 'CUSTOM', selected: false}
    ];

    statuses = [
        {status: 'ALL', selected: true},
        {status: 'LOAD_OK', selected: false},
        {status: 'VALIDATE_OK', selected: false},
        {status: 'VALIDATE_ERROR', selected: false},
        {status: 'EXPORT_OK', selected: false},
        {status: 'DELIVER_OK', selected: false}
    ];

    lastErrors = [
        {lastError: 'ALL', selected: true},
        {lastError: 'BIS3_XSD', selected: false},
        {lastError: 'BIS3_SCH', selected: false},
        {lastError: 'OIOUBL_XSD', selected: false},
        {lastError: 'OIOUBL_SCH', selected: false},
        {lastError: 'CII_XSD', selected: false},
        {lastError: 'CII_SCH', selected: false}
    ];

    documentTypes = [
        {documentType: 'ALL', selected: true},
        {documentType: 'UNSUPPORTED', selected: false},
        {documentType: 'INVOICE', selected: false},
        {documentType: 'CREDITNOTE', selected: false}
    ];

    ingoingFormats = [
        {ingoingFormat: 'ALL', selected: true},
        {ingoingFormat: 'BIS3_INVOICE', selected: false},
        {ingoingFormat: 'BIS3_CREDITNOTE', selected: false},
        {ingoingFormat: 'OIOUBL_INVOICE', selected: false},
        {ingoingFormat: 'OIOUBL_CREDITNOTE', selected: false}
    ];

    constructor(private docs: DocumentsService) {
        this.pagination = new PaginationModel();
        this.pagination.setPagination(
            {
                collectionSize: this.docs.getCollectionSize(),
                currentPage: 1,
                pageSize: 10,
                previousPage: 1
            });
        this.selectedStatus = {status: 'ALL', selected: true};
        this.selectedLastError = {lastError: 'ALL', selected: true};
        this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
        this.selectedDocumentType = {documentType: 'ALL', selected: true};
        this.selectedPageSize = {pageSize: 10, selected: true};
        this.filter = {
            status: this.selectedStatus.status,
            lastError: this.selectedLastError.lastError,
            ingoingFormat: this.selectedIngoingFormat.ingoingFormat,
            organisation: null,
            receiver: null,
            documentType: this.selectedDocumentType.documentType,
            senderName: null,
            receiverName: null,
            dateReceived: new DateRangeModel(new Date(), new Date()),
            dateIssued: new DateRangeModel(new Date(), new Date())
        };
        this.documents = this.docs.getDocumentsAfterFilter(this.pagination.currentPage - 1, this.pagination.pageSize, this.filter);
        this.initCountClicks();
    }

    ngOnInit() {
    }

    loadPage(page: number) {
        if (page === this.pagination.previousPage) {
            this.pagination.previousPage = page;
        } else {
            this.pagination.previousPage = page - 1;
        }
        this.documents = this.docs.getDocumentsAfterFilter(this.pagination.currentPage - 1, this.pagination.pageSize, this.filter);
        this.pagination.pageSize = this.selectedPageSize.pageSize;
        this.pagination.collectionSize = this.docs.getCollectionSize();
        this.initCountClicks();
    }

    loadPageSize() {
        this.pagination.pageSize = this.selectedPageSize.pageSize;
        this.documents = this.docs.getDocumentsAfterFilter(this.pagination.currentPage - 1, this.pagination.pageSize, this.filter);
    }

    loadIngoingFormat() {
        if (this.selectedIngoingFormat === null) {
            this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
        }
        this.filter.ingoingFormat = this.selectedIngoingFormat.ingoingFormat;
        this.filterResult();
    }

    loadTextOrganisation(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.organisation = null;
        } else {
            this.filter.organisation = text;
        }
        this.filterResult();
    }

    loadTextReceiver(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiver = null;
        } else {
            this.filter.receiver = text;
        }
        this.filterResult();
    }

    loadStatus() {
        if (this.selectedStatus === null) {
            this.selectedStatus = {status: 'ALL', selected: true};
        }
        this.filter.status = this.selectedStatus.status;
        this.filterResult();
    }

    loadLastErrors() {
        if (this.selectedLastError === null) {
            this.selectedLastError = {lastError: 'ALL', selected: true};
        }
        this.filter.lastError = this.selectedLastError.lastError;
        this.filterResult();
    }

    loadDocumentType() {
        if (this.selectedDocumentType === null) {
            this.selectedDocumentType = {documentType: 'ALL', selected: true};
        }
        this.filter.documentType = this.selectedDocumentType.documentType;
        this.filterResult();
    }

    loadTextSenderName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.senderName = null;
        } else {
            this.filter.senderName = text;
        }
        this.filterResult();
    }

    loadTextReceiverName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.receiverName = null;
        } else {
            this.filter.receiverName = text;
        }
        this.filterResult();
    }

    loadReceivedDate(date: Date[]) {
        this.filter.dateReceived = new DateRangeModel(date[0], date[1]);
        this.filterResult();
    }

    loadIssuedDate(date: Date[]) {
        this.filter.dateIssued = new DateRangeModel(date[0], date[1]);
        this.filterResult();
    }

    clickNumber() {
        console.log('clickNumber');
        this.documents.reverse();
    }

    clickOrganisation() {
        console.log('clickOrganisation');
        this.countClickOrganisation++;
        if (this.countClickOrganisation === 1) {
            this.documents.sort((one, two) => (one.organisation < two.organisation ? -1 : 1));
        } else if (this.countClickOrganisation === 2) {
            this.documents.sort((one, two) => (one.organisation > two.organisation ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickReceiver() {
        console.log('clickReceiver');
        this.countClickReceiver++;
        if (this.countClickReceiver === 1) {
            this.documents.sort((one, two) => (one.receiver < two.receiver ? -1 : 1));
        } else if (this.countClickReceiver === 2) {
            this.documents.sort((one, two) => (one.receiver > two.receiver ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickStatus() {
        console.log('clickStatus');
        this.countClickStatus++;
        if (this.countClickStatus === 1) {
            this.documents.sort((one, two) => (one.status < two.status ? -1 : 1));
        } else if (this.countClickStatus === 2) {
            this.documents.sort((one, two) => (one.status > two.status ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickLastError() {
        console.log('clickLastError');
        this.countClickLastError++;
        if (this.countClickLastError === 1) {
            this.documents.sort((one, two) => (one.lastError < two.lastError ? -1 : 1));
        } else if (this.countClickLastError === 2) {
            this.documents.sort((one, two) => (one.lastError > two.lastError ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickDocumentType() {
        console.log('clickDocumentType');
        this.countClickDocumentType++;
        if (this.countClickDocumentType === 1) {
            this.documents.sort((one, two) => (one.documentType < two.documentType ? -1 : 1));
        } else if (this.countClickDocumentType === 2) {
            this.documents.sort((one, two) => (one.documentType > two.documentType ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickIngoingFormat() {
        console.log('clickIngoingFormat');
        this.countClickIngoingFormat++;
        if (this.countClickIngoingFormat === 1) {
            this.documents.sort((one, two) => (one.ingoingFormat < two.ingoingFormat ? -1 : 1));
        } else if (this.countClickIngoingFormat === 2) {
            this.documents.sort((one, two) => (one.ingoingFormat > two.ingoingFormat ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickReceived() {
        console.log('clickReceived');
        this.countClickReceived++;
        if (this.countClickReceived === 1) {
            this.documents.sort((one, two) => (one.received < two.received ? -1 : 1));
        } else if (this.countClickReceived === 2) {
            this.documents.sort((one, two) => (one.received > two.received ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickIssued() {
        console.log('clickIssued');
        this.countClickIssued++;
        if (this.countClickIssued === 1) {
            this.documents.sort((one, two) => (one.issued < two.issued ? -1 : 1));
        } else if (this.countClickIssued === 2) {
            this.documents.sort((one, two) => (one.issued > two.issued ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickSenderName() {
        console.log('clickSenderName');
        this.countClickSenderName++;
        if (this.countClickSenderName === 1) {
            this.documents.sort((one, two) => (one.senderName < two.senderName ? -1 : 1));
        } else if (this.countClickSenderName === 2) {
            this.documents.sort((one, two) => (one.senderName > two.senderName ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    clickReceiverName() {
        console.log('clickReceiverName');
        this.countClickReceiverName++;
        if (this.countClickReceiverName === 1) {
            this.documents.sort((one, two) => (one.receiverName < two.receiverName ? -1 : 1));
        } else if (this.countClickReceiverName === 2) {
            this.documents.sort((one, two) => (one.receiverName > two.receiverName ? -1 : 1));
        } else {
            this.loadPage(this.pagination.currentPage);
        }
    }

    filterResult() {
        if (this.selectedPageSize === null) {
            this.selectedPageSize = {pageSize: 10, selected: true};
        }
        this.loadPage(this.pagination.currentPage);
    }

    initCountClicks() {
        this.countClickOrganisation = 0;
        this.countClickReceiver = 0;
        this.countClickStatus = 0;
        this.countClickLastError = 0;
        this.countClickDocumentType = 0;
        this.countClickIngoingFormat = 0;
        this.countClickReceived = 0;
        this.countClickIssued = 0;
        this.countClickSenderName = 0;
        this.countClickReceiverName = 0;
    }
}
