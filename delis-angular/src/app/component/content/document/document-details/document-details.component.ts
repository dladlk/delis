import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { ErrorModel } from '../../../../model/system/error.model';
import { DocumentBytesModel } from '../../../../model/content/document/document-bytes.model';
import { JournalDocumentErrorModel } from '../../../../model/content/document/journal-document-error.model';
import { JournalDocumentModel } from '../../../../model/content/document/journal-document.model';
import { DocumentModel } from '../../../../model/content/document/document.model';
import { LocaleService } from '../../../../service/system/locale.service';
import { ErrorService } from '../../../../service/system/error.service';
import { DocumentService } from '../../../../service/content/document.service';
import { DocumentStateService} from "../../../../service/state/document-state.service";
import { JournalDocumentService } from '../../../../service/content/journal-document.service';
import { FileSaverService } from '../../../../service/system/file-saver.service';
import { ErrorDictionaryModel } from '../../../../model/content/document/error-dictionary.model';

import { DOCUMENT_PATH, SHOW_DATE_FORMAT } from '../../../../app.constants';

@Component({
  selector: 'app-document-details',
  templateUrl: './document-details.component.html',
  styleUrls: ['./document-details.component.scss']
})
export class DocumentDetailsComponent implements OnInit {

  document: DocumentModel = new DocumentModel();
  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
  DOCUMENT_PATH = DOCUMENT_PATH;
  journalDocuments: JournalDocumentModel[] = [];
  journalDocumentErrors: JournalDocumentErrorModel[] = [];
  documentBytesModels: DocumentBytesModel[] = [];

  errorOneDocument = false;
  errorOneDocumentModel: ErrorModel;

  errorDocumentBytes = false;
  errorDocumentBytesModel: ErrorModel;

  errorJournalDocuments = false;
  errorJournalDocumentsModel: ErrorModel;

  errorDownload = false;
  errorDownloadModel: ErrorModel;

  documentId: number;

  isNextUp: boolean;
  isNextDown: boolean;
  currentIds: number[];

  constructor(private location: Location,
              private translate: TranslateService,
              private locale: LocaleService,
              private route: ActivatedRoute,
              private router: Router,
              private errorService: ErrorService,
              private documentService: DocumentService,
              public stateService: DocumentStateService,
              private journalDocumentService: JournalDocumentService) { }

  ngOnInit() {
    const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
    this.documentId = id;
    this.documentService.getOneDocumentById(id).subscribe((data: DocumentModel) => {
      this.document = data;
    }, error => {
      this.errorOneDocumentModel = this.errorService.errorProcess(error);
      this.errorOneDocument = true;
    });
    this.documentService.getListDocumentBytesByDocumentId(id).subscribe((data: {}) => {
      this.documentBytesModels = data['items'];
    }, error => {
      this.errorDocumentBytesModel = this.errorService.errorProcess(error);
      this.errorDocumentBytes = true;
    });
    this.journalDocumentService.getAllByDocumentId(id).subscribe(
      (data: {}) => {
        this.journalDocuments = data['items'];
      }, error => {
        this.errorJournalDocumentsModel = this.errorService.errorProcess(error);
        this.errorJournalDocuments = true;
      }
    );
    this.journalDocumentService.getByJournalDocumentDocumentId(id).subscribe(
      (data: {}) => {
        this.journalDocumentErrors = data['items'];
      }, error => {
        this.errorService.errorProcess(error);
      }
    );
    this.initStateDetails(id);
  }

  private initStateDetails(id: number) {
    if (this.stateService.getFilter() !== undefined) {
      let stateDetails = this.stateService.getFilter().detailsState;
      this.currentIds = stateDetails.currentIds;
      if (this.currentIds.length !== 0) {
        this.isNextUp = id !== this.currentIds[0];
        this.isNextDown = id !== this.currentIds[this.currentIds.length - 1];
      }
    } else {
      this.router.navigate(['/' + DOCUMENT_PATH], { queryParams: { skip: false } });
    }
  }

  download(id: number) {
    this.documentService.downloadFileByDocumentAndDocumentBytes(this.document.id, id).subscribe(response => {
        const filename = FileSaverService.getFileNameFromResponseContentDisposition(response);
        FileSaverService.saveFile(response.body, filename);
        this.errorDownload = false;
      },
      error => {
        this.errorDownloadModel = this.errorService.errorProcess(error);
        this.errorDownload = true;
      }
    );
  }

  getErrorDictionaryModel(id: number): ErrorDictionaryModel[] {
    const err = this.journalDocumentErrors.filter(value => value.journalDocument.id === id);
    if (err === null) {
      return [];
    } else {
      return err.map(value => value.errorDictionary);
    }
  }
}
