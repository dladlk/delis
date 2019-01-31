import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { DocumentsService } from "../../services/documents.service";
import { LocaleService } from "../../../../../service/locale.service";
import { HeaderModel } from "../../../../components/header/header.model";
import { DocumentModel } from "../../models/document.model";
import { ErrorService } from "../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";

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

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private documentService: DocumentsService) {
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
    }
}
