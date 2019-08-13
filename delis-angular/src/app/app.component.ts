import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { environment } from '../environments/environment';
import { RuntimeConfigService } from './service/system/runtime-config.service';
import { LocaleService } from './service/system/locale.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  version = environment.version;

  constructor(
    private configService: RuntimeConfigService,
    private translate: TranslateService,
    private locale: LocaleService) {
      this.translate.setDefaultLang('en');
      let currentLang = 'en';
      if (locale.getLocale().match(/en|da/)) {
        currentLang = locale.getLocale();
      }
      this.translate.use(currentLang);
      const currentVersion = localStorage.getItem('appVersion');
      if (currentVersion === null || currentVersion !== this.version) {
          localStorage.clear();
          localStorage.setItem('appVersion', this.version);
      }
      this.configService.getUrl();
  }

  ngOnInit() { }
}
