import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { DocumentsService } from "../../services/documents.service";
import { JournalDocumentService } from "../../../journal/document/services/journal.document.service";
import { LocaleService } from "../../../../../service/locale.service";
import { HeaderModel } from "../../../../components/header/header.model";
import { DocumentModel } from "../../models/document.model";
import { ErrorService } from "../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";
import { JournalDocumentModel } from "../../../journal/document/models/journal.document.model";
import { ErrorDictionaryModel } from "../../../journal/document/models/error.dictionary.model";
import { DocumentBytesModel } from "../../models/document.bytes.model";

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    id: number;

    pageHeaders: HeaderModel[] = [];
    document: DocumentModel = new DocumentModel();
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    journalDocuments: JournalDocumentModel[];
    journalDocumentErrors: ErrorDictionaryModel[] = [];
    documentBytesModels: DocumentBytesModel[] = [];

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private documentService: DocumentsService,
        private journalDocumentService: JournalDocumentService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/documents', heading: 'documents.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.documentService.getOneDocumentById(id).subscribe((data: DocumentModel) => {
            this.document = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
        this.documentService.getListDocumentBytesByDocumentId(id).subscribe((data: []) => {
            this.documentBytesModels = data["items"];
        }, error => {
            this.errorService.errorProcess(error);
        });
        this.id = id;
        this.initProcess(id);
    }

    private initProcess(id: number) {
        this.currentProdJournalDocuments(id);
        this.currentProdJournalDocumentDocumentId(id);
    }

    private currentProdJournalDocuments(id: number) {
        this.journalDocumentService.getAllByDocumentId(id).subscribe(
            (data: {}) => {
                this.journalDocuments = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }

    private currentProdJournalDocumentDocumentId(id: number) {
        this.journalDocumentService.getByJournalDocumentDocumentId(id).subscribe(
            (data: {}) => {
                this.journalDocumentErrors = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }
}
