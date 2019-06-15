import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

import { routerTransition } from "../../../../../../router.animations";
import { HeaderModel } from "../../../../../components/header/header.model";
import { LocaleService } from "../../../../../../service/locale.service";
import { JournalOrganisationService } from "../../services/journal.organisation.service";
import { ErrorService } from "../../../../../../service/error.service";
import { JournalOrganisationModel } from "../../models/journal.organisation.model";
import { SHOW_DATE_FORMAT } from "../../../../../../app.constants";

@Component({
    selector: 'app-journal-one-organisation',
    templateUrl: './journal.one.organisation.component.html',
    styleUrls: ['./journal.one.organisation.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneOrganisationComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
    journalOrganisation: JournalOrganisationModel = new JournalOrganisationModel();

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private journalOrganisationService: JournalOrganisationService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-organisations', heading: 'journal.organisations.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.journalOrganisationService.getOneJournalOrganisationById(id).subscribe((data: JournalOrganisationModel) => {
            this.journalOrganisation = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
    }
}
