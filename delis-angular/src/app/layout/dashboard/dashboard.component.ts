import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../router.animations';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../service/locale.service";
import { DashboardModel } from "./dashboard.model";
import { DashboardService } from "./dashboard.service";
import { environment } from "../../../environments/environment";
import { ErrorService } from "../../service/error.service";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    animations: [routerTransition()]
})
export class DashboardComponent implements OnInit {

    env = environment;
    dashboardModel: DashboardModel;

    constructor(
        private dashboardService: DashboardService,
        private translate: TranslateService,
        private errorService: ErrorService,
        private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {
        if (this.env.production) {
            this.dashboardService.getDashboardModel().subscribe(
                (data: {}) => {
                    this.dashboardModel = data["data"];
                }, error => {
                    this.errorService.errorProcess(error);
                }
            );
        } else {
            this.dashboardModel = new DashboardModel();
        }
    }
}
