import { Component, OnInit} from "@angular/core";
import { ActivatedRoute, Router} from "@angular/router";
import { TranslateService} from "@ngx-translate/core";

import { routerTransition} from "../../../../../../router.animations";
import { environment} from "../../../../../../../environments/environment";
import { HeaderModel} from "../../../../../components/header/header.model";
import { LocaleService} from "../../../../../../service/locale.service";
import { JournalDocumentService} from "../../services/journal.document.service";
import { JournalDocumentTestGuiStaticService} from "../../services/journal.document.test-gui-static.service";
import { JournalDocumentModel } from "../../models/journal.document.model";

@Component({
    selector: 'app-journal-one-documents',
    templateUrl: './journal.one.document.component.html',
    styleUrls: ['./journal.one.document.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneDocumentComponent implements OnInit {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    journalDocument: any;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
        private journalDocumentService: JournalDocumentService,
        private journalDocumentTestGuiStaticService: JournalDocumentTestGuiStaticService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-documents', heading: 'journal.documents.header', icon: 'fa fa-book'}
        );
    }
    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        if (this.env.production) {
            this.journalDocumentService.getOneJournalDocumentById(id).subscribe((data: JournalDocumentModel) => {
                this.journalDocument = data;
            });
        } else {
            this.journalDocument = Object.assign({}, this.journalDocumentTestGuiStaticService.getOneJournalDocumentById(id));
        }
    }
}