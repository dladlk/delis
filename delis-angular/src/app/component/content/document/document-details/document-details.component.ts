import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

import { ErrorModel } from '../../../../model/system/error.model';
import { DocumentBytesModel } from '../../../../model/content/document/document-bytes.model';
import { JournalDocumentErrorModel } from '../../../../model/content/document/journal-document-error.model';
import { JournalDocumentModel } from '../../../../model/content/document/journal-document.model';
import { DocumentModel } from '../../../../model/content/document/document.model';
import { LocaleService } from '../../../../service/system/locale.service';
import { ErrorService } from '../../../../service/system/error.service';
import { DocumentService } from '../../../../service/content/document.service';
import { DocumentStateService} from '../../../../service/state/document-state.service';
import { JournalDocumentService } from '../../../../service/content/journal-document.service';
import { FileSaverService } from '../../../../service/system/file-saver.service';
import { ErrorDictionaryModel } from '../../../../model/content/document/error-dictionary.model';
import { RuntimeConfigService } from 'src/app/service/system/runtime-config.service';
import { DocumentErrorService } from '../document-error.service';
import { DelisEntityDetailsObservable } from '../../../../observable/delis-entity-details.observable';

import { DOCUMENT_PATH, DASHBOARD_PATH, SHOW_DATE_FORMAT } from '../../../../app.constants';

@Component({
  selector: 'app-document-details',
  templateUrl: './document-details.component.html',
  styleUrls: ['./document-details.component.scss']
})
export class DocumentDetailsComponent implements OnInit, OnDestroy {

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

  hideForm: boolean;

  isNextUp: boolean;
  isNextDown: boolean;
  currentIds: number[];

  statusErrors: string[] = [];

  isShowFooter: boolean;
  topPosToStartShowing = 100;

  private pageUpdate$: Subscription;

  @HostListener('window:scroll')
  checkScroll() {
    const scrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
    this.isShowFooter = scrollPosition >= this.topPosToStartShowing;
  }

  constructor(private location: Location,
              private translate: TranslateService,
              private locale: LocaleService,
              private route: ActivatedRoute,
              private router: Router,
              private errorService: ErrorService,
              private documentService: DocumentService,
              public stateService: DocumentStateService,
              private documentErrorService: DocumentErrorService,
              private configService: RuntimeConfigService,
              private journalDocumentService: JournalDocumentService,
              private delisEntityDetailsObservable: DelisEntityDetailsObservable) {
    this.pageUpdate$ = this.delisEntityDetailsObservable.listen().subscribe((id: any) => {
      this.documentId = id;
      this.stateService.filter.detailsState.currentId = this.documentId;
      this.initPage(id);
      this.initStateDetails(id);
    });
  }

  ngOnInit() {
    if (this.stateService.getFilter() !== undefined) {
      this.documentId = this.stateService.getFilter().detailsState.currentId;
      this.initPage(this.documentId);
      this.initStateDetails(this.documentId);
    } else {
      this.router.navigate(['/' + DASHBOARD_PATH]);
    }
    this.statusErrors = this.documentErrorService.statusErrors;
    this.hideForm = this.configService.getCurrentUser().disabledIrForm;
  }

  ngOnDestroy() {
    if (this.pageUpdate$) {
      this.pageUpdate$.unsubscribe();
    }
  }

  private initPage(id: any) {
    this.documentService.getOneDocumentById(id).subscribe((data: DocumentModel) => {
      this.document = data;
    }, error => {
      this.errorOneDocumentModel = this.errorService.errorProcess(error);
      this.errorOneDocument = true;
    });
    this.documentService.getListDocumentBytesByDocumentId(id).subscribe((data: any) => {
      this.documentBytesModels = data.items;
    }, error => {
      this.errorDocumentBytesModel = this.errorService.errorProcess(error);
      this.errorDocumentBytes = true;
    });
    this.journalDocumentService.getAllByDocumentId(id).subscribe((data: any) => {
          this.journalDocuments = data.items;
        }, error => {
          this.errorJournalDocumentsModel = this.errorService.errorProcess(error);
          this.errorJournalDocuments = true;
        }
    );
    this.journalDocumentService.getByJournalDocumentDocumentId(id).subscribe((data: any) => {
          this.journalDocumentErrors = data.items;
        }, error => {
          this.errorService.errorProcess(error);
        }
    );
  }

  private initStateDetails(id: number) {
    if (this.stateService.getFilter() !== undefined) {
      const stateDetails = this.stateService.getFilter().detailsState;
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
