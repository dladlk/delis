import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {DocumentModel} from '../../../model/content/document/document.model';
import {ErrorModel} from '../../../model/system/error.model';
import {DocumentBytesModel} from '../../../model/content/document/document-bytes.model';
import {JournalDocumentErrorModel} from '../../../model/content/document/journal-document-error.model';
import {JournalDocumentModel} from '../../../model/content/document/journal-document.model';
import {SHOW_DATE_FORMAT} from '../../../app.constants';
import {LocaleService} from '../../../service/system/locale.service';
import {ErrorService} from '../../../service/system/error.service';
import {DocumentService} from '../../../service/content/document.service';
import {JournalDocumentService} from '../../../service/content/journal-document.service';
import {ErrorDictionaryModel} from '../../../model/content/document/error-dictionary.model';
import {FileSaverService} from '../../../service/system/file-saver.service';

@Component({
  selector: 'app-document-details',
  templateUrl: './document-details.component.html',
  styleUrls: ['./document-details.component.scss']
})
export class DocumentDetailsComponent implements OnInit {

  document: DocumentModel = new DocumentModel();
  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
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

  constructor(private location: Location,
              private translate: TranslateService,
              private locale: LocaleService,
              private route: ActivatedRoute,
              private router: Router,
              private errorService: ErrorService,
              private documentService: DocumentService,
              private journalDocumentService: JournalDocumentService) {
  }

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

  back() {
    this.location.back();
  }

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/document/', this.documentId]));
  }
}
