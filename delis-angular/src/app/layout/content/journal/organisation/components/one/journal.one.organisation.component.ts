import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

import { routerTransition } from "../../../../../../router.animations";
import { environment } from "../../../../../../../environments/environment";
import { HeaderModel } from "../../../../../components/header/header.model";
import { LocaleService } from "../../../../../../service/locale.service";
import { JournalOrganisationService } from "../../services/journal.organisation.service";
import { JournalOrganisationTestGuiStaticService } from "../../services/journal.organisation.test-gui-static.service";
import { ErrorService } from "../../../../../../service/error.service";
import { JournalOrganisationModel } from "../../models/journal.organisation.model";

@Component({
    selector: 'app-journal-one-organisation',
    templateUrl: './journal.one.organisation.component.html',
    styleUrls: ['./journal.one.organisation.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneOrganisationComponent implements OnInit {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    journalOrganisation: JournalOrganisationModel = new JournalOrganisationModel();

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private journalOrganisationService: JournalOrganisationService,
        private journalOrganisationTestGuiStaticService: JournalOrganisationTestGuiStaticService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-organisations', heading: 'journal.organisations.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        if (this.env.production) {
            this.journalOrganisationService.getOneJournalOrganisationById(id).subscribe((data: JournalOrganisationModel) => {
                this.journalOrganisation = data;
            }, error => {
                this.errorService.errorProcess(error);
            });
        } else {
            this.journalOrganisation = Object.assign({}, this.journalOrganisationTestGuiStaticService.getOneJournalOrganisationById(id));
        }
    }
}
