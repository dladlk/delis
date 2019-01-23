import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { DocumentsTestGuiStaticService } from "../../services/documents.test-gui-static.service";
import { environment } from "../../../../../../environments/environment";
import { DocumentsService } from "../../services/documents.service";
import { LocaleService } from "../../../../../service/locale.service";
import { HeaderModel } from "../../../../components/header/header.model";
import { DocumentModel } from "../../models/document.model";
import { ErrorService } from "../../../../../service/error.service";

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    env = environment;
    pageHeaders: HeaderModel[] = [];
    document: DocumentModel = new DocumentModel();

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private documentService: DocumentsService,
        private documentsStaticService: DocumentsTestGuiStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/documents', heading: 'documents.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        if (this.env.production) {
            this.documentService.getOneDocumentById(id).subscribe((data: DocumentModel) => {
                this.document = data;
            }, error => {
                this.errorService.errorProcess(error);
            });
        } else {
            this.document = Object.assign({}, this.documentsStaticService.getOneDocument(id));
        }
    }
}