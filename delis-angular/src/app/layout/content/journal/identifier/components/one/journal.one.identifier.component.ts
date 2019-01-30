import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

import { routerTransition } from "../../../../../../router.animations";
import { HeaderModel } from "../../../../../components/header/header.model";
import { LocaleService } from "../../../../../../service/locale.service";
import { JournalIdentifierService } from "../../services/journal.identifier.service";
import { JournalIdentifierModel } from "../../models/journal.identifier.model";
import { ErrorService } from "../../../../../../service/error.service";

@Component({
    selector: 'app-journal-one-identifier',
    templateUrl: './journal.one.identifier.component.html',
    styleUrls: ['./journal.one.identifier.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneIdentifierComponent {

    pageHeaders: HeaderModel[] = [];
    journalIdentifier: JournalIdentifierModel = new JournalIdentifierModel();

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private journalIdentifierService: JournalIdentifierService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-identifiers', heading: 'journal.identifier.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.journalIdentifierService.getOneJournalIdentifierById(id).subscribe((data: JournalIdentifierModel) => {
            this.journalIdentifier = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
    }
}