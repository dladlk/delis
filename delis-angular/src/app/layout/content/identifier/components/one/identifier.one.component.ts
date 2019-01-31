import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

import { routerTransition } from "../../../../../router.animations";
import { HeaderModel } from "../../../../components/header/header.model";
import { IdentifierModel } from "../../models/identifier.model";
import { LocaleService } from "../../../../../service/locale.service";
import { ErrorService } from "../../../../../service/error.service";
import { IdentifierService } from "../../services/identifier.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";

@Component({
    selector: 'app-identifiers-one',
    templateUrl: './identifier.one.component.html',
    styleUrls: ['./identifier.one.component.scss'],
    animations: [routerTransition()]
})
export class IdentifierOneComponent {

    pageHeaders: HeaderModel[] = [];
    identifier: IdentifierModel = new IdentifierModel();

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private identifierService: IdentifierService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/identifiers', heading: 'identifier.header', icon: 'fa fa-id-card'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.identifierService.getOneIdentifierById(id).subscribe((data: IdentifierModel) => {
            this.identifier = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
    }
}
