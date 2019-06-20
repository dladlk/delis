import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { routerTransition } from '../../router.animations';
import { LocaleService } from '../../service/locale.service';
import { DashboardModel } from './dashboard.model';
import { DashboardService } from './dashboard.service';
import { ErrorService } from '../../service/error.service';
import { RefreshService } from '../../service/refresh.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    animations: [routerTransition()]
})
export class DashboardComponent implements OnInit {

    dashboardModel: DashboardModel = new DashboardModel();
    show: boolean;

    constructor(
        private refreshService: RefreshService,
        private router: Router,
        private dashboardService: DashboardService,
        private translate: TranslateService,
        private errorService: ErrorService,
        private locale: LocaleService) {
        this.show = false;
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.dashboardService.getDashboardModel().subscribe(
            (data: {}) => {
                this.dashboardModel = data['data'];
                this.show = true;
            }, error => {
                this.errorService.errorProcess(error);
                this.show = false;
            }
        );
        this.refreshService.listen().subscribe(() => {
            this.refreshData();
        });
    }

    ngOnInit() {}

    refreshData() {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/dashboard']));
    }
}
