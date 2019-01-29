import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";
import { routerTransition } from "../../../../../router.animations";
import { environment } from "../../../../../../environments/environment";
import { HeaderModel } from "../../../../components/header/header.model";
import { IdentifierModel } from "../../models/identifier.model";
import { LocaleService } from "../../../../../service/locale.service";
import { ErrorService } from "../../../../../service/error.service";
import { IdentifierService } from "../../services/identifier.service";

@Component({
    selector: 'app-identifiers-one',
    templateUrl: './identifier.one.component.html',
    styleUrls: ['./identifier.one.component.scss'],
    animations: [routerTransition()]
})
export class IdentifierOneComponent {

    env = environment;
    pageHeaders: HeaderModel[] = [];
    identifier: IdentifierModel = new IdentifierModel();

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
        if (this.env.production) {
            this.identifierService.getOneIdentifierById(id).subscribe((data: IdentifierModel) => {
                this.identifier = data;
            }, error => {
                this.errorService.errorProcess(error);
            });
        }
    }
}