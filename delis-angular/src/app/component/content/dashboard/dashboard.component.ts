import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { RefreshObservable } from '../../../observable/refresh.observable';
import { DashboardModel } from '../../../model/content/dashboard.model';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { TokenService } from '../../../service/system/token.service';
import { ErrorService } from '../../../service/system/error.service';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  dashboardModel: DashboardModel = new DashboardModel();
  private refreshUpdate$: Subscription;
  private readonly url: string;

  constructor(private router: Router,
              private tokenService: TokenService,
              private configService: RuntimeConfigService,
              private httpRestService: HttpRestService,
              private errorService: ErrorService,
              private refreshObservable: RefreshObservable) {
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/dashboard';
    this.refreshUpdate$ = this.refreshObservable.listen().subscribe(() => this.refreshData());
  }

  ngOnInit() {
    this.httpRestService.methodGet(this.url, null, this.tokenService.getToken()).subscribe(
        (data: {}) => {
          this.dashboardModel = data['data'];
        }, error => {
          this.errorService.errorProcess(error);
        }
    );
  }

  ngOnDestroy() {
    if (this.refreshUpdate$) {
      this.refreshUpdate$.unsubscribe();
    }
  }

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/dashboard']));
  }
}
