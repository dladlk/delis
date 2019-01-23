import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthorizationService } from '../../../login/authorization.service';
import { LocaleService } from "../../../service/locale.service";
import {RuntimeConfigService} from "../../../service/runtime.config.service";
import {ErrorService} from "../../../service/error.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  public pushRightClass: string;
  public lang: string;
  public username: string;

  constructor(
    private auth: AuthorizationService,
    private translate: TranslateService,
    private locale: LocaleService,
    private configService: RuntimeConfigService,
    private errorService: ErrorService,
    public router: Router) {

    this.lang = locale.getlocale();
    this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');

    this.router.events.subscribe(val => {
      if (
        val instanceof NavigationEnd &&
        window.innerWidth <= 992 &&
        this.isToggled()
      ) {
        this.toggleSidebar();
      }
    });
  }

  ngOnInit() {
    this.pushRightClass = 'push-right';
    this.username = this.configService.getCurrentUser();
  }

  isToggled(): boolean {
    const dom: Element = document.querySelector('body');
    return dom.classList.contains(this.pushRightClass);
  }

  toggleSidebar() {
    const dom: any = document.querySelector('body');
    dom.classList.toggle(this.pushRightClass);
  }

  onLoggedout() {
    this.errorService.logout();
  }

  changeLang(language: string) {
    this.translate.use(language);
    this.translate.setDefaultLang(language);
    this.locale.setLocale(language);
    this.lang = language;
  }
}
