import { Component, OnInit } from '@angular/core';

import { routerTransition } from '../../../router.animations';
import { DocumentsService } from '../services/documents.service';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import { DateRangeModel } from '../../../models/date.range.model';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss'],
  animations: [routerTransition()]
})
export class DocumentsComponent implements OnInit {

  collectionSize: number;
  currentPage: number;
  previousPage: number;
  pageSize: number;
  selectedStatus: any;
  selectedLastError: any;
  selectedIngoingFormat: any;
  selectedPageSize: any;
  textOrganisation: string;
  textReceiver: string;
  textType: string;
  textSenderName: string;
  textReceiverName: string;

  documents: DocumentsModel[];
  filter: FilterProcessResult;

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

  ingoingFormats = [
    {ingoingFormat: 'ALL', selected: true},
    {ingoingFormat: 'BIS3_INVOICE', selected: false},
    {ingoingFormat: 'BIS3_CREDITNOTE', selected: false},
    {ingoingFormat: 'OIOUBL_INVOICE', selected: false},
    {ingoingFormat: 'OIOUBL_CREDITNOTE', selected: false}
  ];

  constructor(private docs: DocumentsService) {
    this.currentPage = 1;
    this.previousPage = 0;
    this.pageSize = 10;
    this.selectedStatus = {status: 'ALL', selected: true};
    this.selectedLastError = {lastError: 'ALL', selected: true};
    this.selectedIngoingFormat = {ingoingFormat: 'ALL', selected: true};
    this.selectedPageSize = {pageSize: 10, selected: true};
    this.collectionSize = this.docs.getCollectionSize();
    this.filter = {
      status: this.selectedStatus.status,
      lastError: this.selectedLastError.lastError,
      ingoingFormat: this.selectedIngoingFormat.ingoingFormat,
      organisation: null,
      receiver: null,
      type: null,
      senderName: null,
      receiverName: null,
      dateReceived: new DateRangeModel(new Date(), new Date()),
      dateIssued: new DateRangeModel(new Date(), new Date())
    };
    this.documents = this.docs.getDocumentsAfterFilter(this.currentPage - 1, this.pageSize, this.filter);
  }

  ngOnInit() {
  }

  loadPage(page: number) {
    if (page === this.previousPage) {
      this.previousPage = page;
    } else {
      this.previousPage = page - 1;
    }
    this.pageSize = this.selectedPageSize.pageSize;
    this.documents = this.docs.getDocumentsAfterFilter(this.currentPage - 1, this.pageSize, this.filter);
  }

  loadPageSize() {
    this.pageSize = this.selectedPageSize.pageSize;
    this.documents = this.docs.getDocumentsAfterFilter(this.currentPage - 1, this.pageSize, this.filter);
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

  loadTextType(text: string) {
    if (text.length === 0 || text == null) {
      this.filter.type = null;
    } else {
      this.filter.type = text;
    }
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

  filterResult() {
    if (this.selectedPageSize === null) {
      this.selectedPageSize = {pageSize: 10, selected: true};
    }
    this.pageSize = this.selectedPageSize.pageSize;
    this.loadPage(this.currentPage);
  }
}
