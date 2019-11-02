import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { RuntimeConfigService } from './service/system/runtime-config.service';
import { LocaleService } from './service/system/locale.service';
import { VersionCheckService } from './service/system/version-check.service';
import { RoutingStateService } from "./service/system/routing-state.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(private configService: RuntimeConfigService,
              private translate: TranslateService,
              private locale: LocaleService,
              private versionCheckService: VersionCheckService,
              private routingState: RoutingStateService) {
    this.translate.setDefaultLang('da');
    let currentLang = 'da';
    if (locale.getLocale().match(/en|da/)) {
      currentLang = locale.getLocale();
    }
    this.translate.use(currentLang);
    this.configService.getUrl();
    this.routingState.loadRouting();
  }

  ngOnInit() {
    this.versionCheckService.initVersionCheck();
  }
}
