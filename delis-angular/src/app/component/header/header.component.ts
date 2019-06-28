import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { LocaleService } from '../../service/system/locale.service';
import { RuntimeConfigService } from '../../service/system/runtime-config.service';
import { TokenService } from '../../service/system/token.service';
import { HttpRestService } from '../../service/system/http-rest.service';
import { ForwardingLanguageObservable } from '../../observable/forwarding-language.observable';
import { ContentSelectInfoService } from '../../service/system/content-select.info.service';
import { RefreshObservable } from '../../observable/refresh.observable';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  public lang: string;
  public username: string;
  private url: string;

  constructor(
    private tokenService: TokenService,
    private http: HttpRestService,
    private router: Router,
    private translate: TranslateService,
    private locale: LocaleService,
    private configService: RuntimeConfigService,
    private contentSelectInfoService: ContentSelectInfoService,
    private forwardingLanguageObservable: ForwardingLanguageObservable,
    private refreshObservable: RefreshObservable) {
    this.lang = locale.getLocale();
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.url = this.configService.getConfigUrl();
    this.username = this.configService.getCurrentUser();
  }

  ngOnInit() { }

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
    this.translate.use(language);
    this.translate.setDefaultLang(language);
    this.locale.setLocale(language);
    this.lang = language;
    this.forwardingLanguageObservable.forwardLanguage(this.lang);
    this.contentSelectInfoService.generateAllContentSelectInfo(this.tokenService.getToken());
    this.contentSelectInfoService.generateUniqueOrganizationNameInfo(this.tokenService.getToken());
    this.refreshObservable.refreshPage();
  }
}
