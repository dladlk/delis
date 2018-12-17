import { Injectable } from '@angular/core';
import { DocumentsModel, FilterProcessResult } from '../models/documents.model';
import * as data from '../documents.json';

@Injectable()
export class DocumentsService {

  private documents: DocumentsModel[] = data.docs;
  private startElement: number;

  constructor() {}

  getDocuments() {
    this.documents = data.docs;
    return this.documents;
  }

  getDocumentsAfterFilter(currentPage: number, sizeElement: number, filter: FilterProcessResult) {
    // console.log('status: ' + filter.status);
    // console.log('lastError: ' + filter.lastError);
    // console.log('ingoingFormat: ' + filter.ingoingFormat);
    // console.log('organisation: ' + filter.organisation);
    // console.log('receiver: ' + filter.receiver);
    // console.log('type: ' + filter.type);
    // console.log('senderName: ' + filter.senderName);
    // console.log('receiverName: ' + filter.receiverName);
    // console.log('dateReceived: ' + filter.dateReceived.dateStart + ' ' + filter.dateReceived.dateEnd);
    // console.log('dateIssued: ' + filter.dateIssued.dateStart + ' ' + filter.dateIssued.dateEnd);

    this.startElement = currentPage * sizeElement;
    this.getDocuments();

    if (filter.status !== 'ALL') {
      this.documents = this.documents.filter(el => el.status === filter.status);
    }

    if (filter.lastError !== 'ALL') {
      this.documents = this.documents.filter(el => el.lastError === filter.lastError);
    }

      if (filter.documentType !== 'ALL') {
          this.documents = this.documents.filter(el => el.documentType === filter.documentType);
      }

    if (filter.ingoingFormat !== 'ALL') {
      this.documents = this.documents.filter(el => el.ingoingFormat === filter.ingoingFormat);
    }

    return this.documents.slice(this.startElement, this.startElement + sizeElement);
  }

  getCollectionSize() {
    return this.documents.length;
  }
}
