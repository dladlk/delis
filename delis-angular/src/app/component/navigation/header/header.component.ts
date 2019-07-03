import {Component, OnInit, Output, EventEmitter, ViewChild} from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MatMenuTrigger } from '@angular/material';

import { TokenService } from '../../../service/system/token.service';
import { HttpRestService } from '../../../service/system/http-rest.service';
import { LocaleService } from '../../../service/system/locale.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { ChangeLangService } from '../../../service/system/change-lang.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  @Output() public sidenavToggle = new EventEmitter();
  @ViewChild(MatMenuTrigger, { static: true }) trigger: MatMenuTrigger;

  public lang: string;
  public username: string;
  private url: string;

  constructor(
    private tokenService: TokenService,
    private http: HttpRestService,
    private router: Router,
    private changeLangService: ChangeLangService,
    private translate: TranslateService,
    private locale: LocaleService,
    private configService: RuntimeConfigService) {
    this.lang = locale.getLocale();
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.url = this.configService.getConfigUrl();
    this.username = this.configService.getCurrentUser();
  }

  ngOnInit() {
  }

  onLoggedOut() {
    this.http.methodDelete(this.url + '/rest/logout', this.tokenService.getToken()).subscribe(
      (data: {}) => {
        console.log('logout : ' + data['data']);
      }
    );
    this.tokenService.resetToken();
    this.configService.resetCurrentUser();
    this.router.navigate(['/login']);
  }

  changeLang(language: string) {
    this.lang = this.changeLangService.changeLang(language);
  }

  public onToggleSidenav = () => {
    this.sidenavToggle.emit();
  }
}
