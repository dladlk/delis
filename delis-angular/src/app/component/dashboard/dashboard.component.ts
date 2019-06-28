import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { TranslateService} from '@ngx-translate/core';

import { DashboardModel } from '../../model/system/dashboard.model';
import { ErrorService } from '../../service/system/error.service';
import { LocaleService } from '../../service/system/locale.service';
import { RefreshObservable } from '../../observable/refresh.observable';
import { TokenService } from '../../service/system/token.service';
import { HttpRestService } from '../../service/system/http-rest.service';
import { RuntimeConfigService } from '../../service/system/runtime-config.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  dashboardModel: DashboardModel = new DashboardModel();

  private url: string;

  constructor(
    private refreshObservable: RefreshObservable,
    private router: Router,
    private tokenService: TokenService,
    private configService: RuntimeConfigService,
    private httpRestService: HttpRestService,
    private translate: TranslateService,
    private errorService: ErrorService,
    private locale: LocaleService) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.url = this.configService.getConfigUrl();
    this.url = this.url + '/rest/dashboard';
    this.httpRestService.methodGet(this.url, null, this.tokenService.getToken()).subscribe(
      (data: {}) => {
        this.dashboardModel = data['data'];
      }, error => {
        this.errorService.errorProcess(error);
      }
    );
    this.refreshObservable.listen().subscribe(() => {
      this.refreshData();
    });
  }

  ngOnInit() {}

  refreshData() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/dashboard']));
  }

}
