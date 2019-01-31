import { Component, OnInit} from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { TranslateService} from "@ngx-translate/core";

import { routerTransition} from "../../../../../../router.animations";
import { HeaderModel} from "../../../../../components/header/header.model";
import { LocaleService} from "../../../../../../service/locale.service";
import { JournalDocumentService} from "../../services/journal.document.service";
import { JournalDocumentModel } from "../../models/journal.document.model";
import { ErrorService } from "../../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../../app.constants";

@Component({
    selector: 'app-journal-one-documents',
    templateUrl: './journal.one.document.component.html',
    styleUrls: ['./journal.one.document.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneDocumentComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
    journalDocument: JournalDocumentModel = new JournalDocumentModel();

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private journalDocumentService: JournalDocumentService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-documents', heading: 'journal.documents.header', icon: 'fa fa-book'}
        );
    }
    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.journalDocumentService.getOneJournalDocumentById(id).subscribe((data: JournalDocumentModel) => {
            this.journalDocument = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
    }
}
