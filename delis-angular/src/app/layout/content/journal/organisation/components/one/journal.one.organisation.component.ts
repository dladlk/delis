import { Component, OnInit } from "@angular/core";
import { routerTransition } from "../../../../../../router.animations";
import { environment } from "../../../../../../../environments/environment";
import { HeaderModel } from "../../../../../components/header/header.model";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../../../service/locale.service";
import { ActivatedRoute, Router } from "@angular/router";
import { JournalOrganisationService } from "../../services/journal.organisation.service";
import { JournalOrganisationTestGuiStaticService } from "../../services/journal.organisation.test-gui-static.service";
import { JournalDocumentModel } from "../../../document/models/journal.document.model";

@Component({
    selector: 'app-journal-one-organisation',
    templateUrl: './journal.one.organisation.component.html',
    styleUrls: ['./journal.one.organisation.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneOrganisationComponent implements OnInit {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    journalOrganisation: any;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private router: Router,
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
            this.journalOrganisationService.getOneJournalOrganisationById(id).subscribe((data: JournalDocumentModel) => {
                this.journalOrganisation = data;
            });
        } else {
            this.journalOrganisation = Object.assign({}, this.journalOrganisationTestGuiStaticService.getOneJournalOrganisationById(id));
        }
    }
}
