import { Component, OnInit } from "@angular/core";
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { routerTransition } from "../../../../router.animations";
import {DocumentsStaticService} from "../../services/documents.static.service";
import {environment} from "../../../../../environments/environment";
import {DocumentsService} from "../../services/documents.service";
import {TranslateService} from "@ngx-translate/core";
import {LocaleService} from "../../../../service/locale.service";
import {DocumentsModel} from "../../models/documents.model";
import {HeaderModel} from "../../../components/header/header.model";

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    document: DocumentsModel;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
        private documentService: DocumentsService,
        private documentsStaticService: DocumentsStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/documents', heading: 'documents.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        // this.route.paramMap.pipe(
        //     switchMap((params: ParamMap) =>
        //         this.service.getOneDocument(params.get('id')))
        // );

        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        if (this.env.production) {
            // this.document = this.documentService.getOneDocument(id);
        } else {
            this.document = this.documentsStaticService.getOneDocument(id);
        }
    }
}
