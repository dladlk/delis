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

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    env = environment;

    document: DocumentsModel;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
        private documentService: DocumentsService,
        private documentsStaticService: DocumentsStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit(): void {
        // this.route.paramMap.pipe(
        //     switchMap((params: ParamMap) =>
        //         this.service.getOneDocument(params.get('id')))
        // );

        let id = this.route.snapshot.paramMap.get('id');
        console.log('id = ' + id);
        if (this.env.production) {
            // this.document = this.documentService.getOneDocument(id);
        } else {
            this.document = this.documentsStaticService.getOneDocument(id);
        }
    }
}
