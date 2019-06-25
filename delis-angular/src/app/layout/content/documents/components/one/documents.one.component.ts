import {Component, OnInit} from '@angular/core';
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

import {routerTransition} from '../../../../../router.animations';
import {DocumentsService} from '../../services/documents.service';
import {JournalDocumentService} from '../../../journal/document/services/journal.document.service';
import {LocaleService} from '../../../../../service/locale.service';
import {HeaderModel} from '../../../../components/header/header.model';
import {DocumentModel} from '../../models/document.model';
import {ErrorService} from '../../../../../service/error.service';
import {SHOW_DATE_FORMAT} from '../../../../../app.constants';
import {JournalDocumentModel} from '../../../journal/document/models/journal.document.model';
import {ErrorDictionaryModel} from '../../../journal/document/models/error.dictionary.model';
import {DocumentBytesModel} from '../../models/document.bytes.model';
import {JournalDocumentErrorModel} from '../../../journal/document/models/journal.document.error.model';
import {FileSaverService} from '../../../../../service/file.saver.service';
import {ErrorModel} from '../../../../../models/error.model';

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
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

    constructor(
        private location: Location,
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
        private errorService: ErrorService,
        private documentService: DocumentsService,
        private journalDocumentService: JournalDocumentService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            {routerLink: '/documents', heading: 'documents.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
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
            this.router.navigate(['/documents/details/', this.documentId]));
    }
}
