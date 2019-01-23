import { Component } from "@angular/core";
import { routerTransition } from "../../../../../../router.animations";
import { environment } from "../../../../../../../environments/environment";
import { HeaderModel } from "../../../../../components/header/header.model";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../../../service/locale.service";
import { ActivatedRoute } from "@angular/router";
import { JournalIdentifierService } from "../../services/journal.identifier.service";
import { JournalIdentifierTestGuiStaticService } from "../../services/journal.identifier.test-gui-static.service";
import { JournalIdentifierModel } from "../../models/journal.identifier.model";
import { ErrorService } from "../../../../../../service/error.service";

@Component({
    selector: 'app-journal-one-identifier',
    templateUrl: './journal.one.identifier.component.html',
    styleUrls: ['./journal.one.identifier.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneIdentifierComponent {

    env = environment;

    pageHeaders: HeaderModel[] = [];
    journalIdentifier: any;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private journalIdentifierService: JournalIdentifierService,
        private journalIdentifierTestGuiStaticService: JournalIdentifierTestGuiStaticService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/journal-identifiers', heading: 'journal.identifier.header', icon: 'fa fa-book'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        if (this.env.production) {
            this.journalIdentifierService.getOneJournalIdentifierById(id).subscribe((data: JournalIdentifierModel) => {
                this.journalIdentifier = data;
            }, error => {
               this.errorService.errorProcess(error);
            });
        } else {
            this.journalIdentifier = Object.assign({}, this.journalIdentifierTestGuiStaticService.getOneJournalIdentifierById(id));
        }
    }
}