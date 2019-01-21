import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../router.animations';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../service/locale.service";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    animations: [routerTransition()]
})
export class DashboardComponent implements OnInit {

    constructor(
        private translate: TranslateService,
        private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {}
}
