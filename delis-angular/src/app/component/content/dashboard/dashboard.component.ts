import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from "@angular/router";
import { HttpRestService } from '../../../service/system/http-rest.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { TokenService } from '../../../service/system/token.service';
import { ErrorService } from '../../../service/system/error.service';
import { DashboardSendDocumentData } from "../../../model/content/dashboard/dashboard-send-document.data";
import { RefreshObservable } from "../../../observable/refresh.observable";
import { LogoutService } from "../../../service/system/logout.service";
import { DashboardDocumentAdminData } from "../../../model/content/dashboard/dashboard-document-admin.data";
import { DashboardDocumentUserData } from "../../../model/content/dashboard/dashboard-document-user.data";

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class DashboardComponent implements OnInit {

    dashboardSendDocumentDataList: DashboardSendDocumentData[] = [];
    dashboardDocumentAdminDataList: DashboardDocumentAdminData[] = [];
    dashboardDocumentUserDataList: DashboardDocumentUserData[] = [];
    dashboardDocumentAdminErrorDataList: DashboardDocumentAdminData[] = [];
    dashboardDocumentUserErrorDataList: DashboardDocumentUserData[] = [];

    panelOpenState = false;

    role: string;

    selected = 0;

    labels = ['dashboard.charts.document', 'dashboard.charts.error', 'dashboard.charts.send'];

    constructor(private router: Router,
                private tokenService: TokenService,
                private configService: RuntimeConfigService,
                private logoutService: LogoutService,
                private httpRestService: HttpRestService,
                private errorService: ErrorService,
                private refreshObservable: RefreshObservable) {

        const role = this.configService.getRole();
        if (role === null) {
            this.logoutService.logout();
        }
        this.role = role;

        this.httpRestService.methodGet(this.configService.getConfigUrl() + '/rest/dashboard/document', null, this.tokenService.getToken()).subscribe(
            (data: {}) => {
                if (this.isUser()) {
                    this.dashboardDocumentUserDataList = data['items'];
                } else {
                    this.dashboardDocumentAdminDataList = data['items'];
                }
            }, error => {
                this.errorService.errorProcess(error);
            }
        );

        this.httpRestService.methodGet(this.configService.getConfigUrl() + '/rest/dashboard/document/error', null, this.tokenService.getToken()).subscribe(
            (data: {}) => {
                if (this.isUser()) {
                    this.dashboardDocumentUserErrorDataList = data['items'];
                } else {
                    this.dashboardDocumentAdminErrorDataList = data['items'];
                }
            }, error => {
                this.errorService.errorProcess(error);
            }
        );

        this.httpRestService.methodGet(this.configService.getConfigUrl() + '/rest/dashboard/send', null, this.tokenService.getToken()).subscribe(
            (data: {}) => {
                this.dashboardSendDocumentDataList = data['items'];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );

        this.refreshObservable.listen().subscribe(() => {
            this.refreshData();
        });
    }

    ngOnInit() { }

    isUser() {
        return this.role === 'ROLE_USER';
    }

    refreshData() {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/dashboard']));
    }

    selectedIndexChange(event: number) {
        this.selected = event;
    }
}
