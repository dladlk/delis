import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from "ngx-spinner";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from '../../router.animations';
import { LocaleService } from "../../service/locale.service";
import { DashboardModel } from "./dashboard.model";
import { DashboardService } from "./dashboard.service";
import { ErrorService } from "../../service/error.service";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    animations: [routerTransition()]
})
export class DashboardComponent implements OnInit {

    dashboardModel: DashboardModel = new DashboardModel();

    constructor(
        private spinner: NgxSpinnerService,
        private dashboardService: DashboardService,
        private translate: TranslateService,
        private errorService: ErrorService,
        private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.spinner.show();
        this.dashboardService.getDashboardModel().subscribe(
            (data: {}) => {
                this.dashboardModel = data["data"];
                setTimeout(() => {
                    this.spinner.hide();
                }, 2000);
            }, error => {
                this.errorService.errorProcess(error);
                setTimeout(() => {
                    this.spinner.hide();
                }, 2000);
            }
        );
    }

    ngOnInit() {}
}
