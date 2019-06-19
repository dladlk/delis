import {Component, OnInit} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';

import { routerTransition } from '../../../../../../router.animations';
import { HeaderModel } from '../../../../../components/header/header.model';
import { LocaleService } from '../../../../../../service/locale.service';
import { JournalIdentifierService } from '../../services/journal.identifier.service';
import { JournalIdentifierModel } from '../../models/journal.identifier.model';
import { ErrorService } from '../../../../../../service/error.service';
import { SHOW_DATE_FORMAT } from '../../../../../../app.constants';
import { ErrorModel } from '../../../../../../models/error.model';

@Component({
    selector: 'app-journal-one-identifier',
    templateUrl: './journal.one.identifier.component.html',
    styleUrls: ['./journal.one.identifier.component.scss'],
    animations: [routerTransition()]
})
export class JournalOneIdentifierComponent implements OnInit {

    pageHeaders: HeaderModel[] = [];
    journalIdentifier: JournalIdentifierModel = new JournalIdentifierModel();

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    errorJournalOneIdentifier = false;
    errorJournalOneIdentifierModel: ErrorModel;

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
        const id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.journalIdentifierService.getOneJournalIdentifierById(id).subscribe((data: JournalIdentifierModel) => {
            this.journalIdentifier = data;
            this.errorJournalOneIdentifier = false;
        }, error => {
            this.errorJournalOneIdentifierModel = this.errorService.errorProcess(error);
            this.errorJournalOneIdentifier = true;
        });
    }
}
