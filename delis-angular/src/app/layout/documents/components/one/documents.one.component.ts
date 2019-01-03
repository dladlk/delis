import { Component, OnInit } from "@angular/core";
import { Router, ActivatedRoute } from '@angular/router';
import { routerTransition } from "../../../../router.animations";
import { DocumentsTestGuiStaticService } from "../../services/documents.test-gui-static.service";
import { environment } from "../../../../../environments/environment";
import { DocumentsService } from "../../services/documents.service";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";
import { HeaderModel } from "../../../components/header/header.model";
import { DocumentFullModel } from "../../models/document.full.model";

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    document: any;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
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
            this.documentService.getOneDocumentById(id).subscribe((data: DocumentFullModel) => {
                this.document = data;
            });
        } else {
            this.document = Object.assign({}, this.documentsStaticService.getOneDocument(id));
        }
    }
}
