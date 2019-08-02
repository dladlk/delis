import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MatMenuTrigger } from '@angular/material';

import { LocaleService } from '../../../service/system/locale.service';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { ChangeLangService } from '../../../service/system/change-lang.service';
import { CurrentUserModel } from "../../../model/system/current-user.model";
import { SHOW_DATE_FORMAT } from "../../../app.constants";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  @Output() public sidenavToggle = new EventEmitter();
  @ViewChild(MatMenuTrigger, { static: true }) trigger: MatMenuTrigger;

  public lang: string;
  public currentUser: CurrentUserModel;
  public headerUserName: string;

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  constructor(
      private changeLangService: ChangeLangService,
      private translate: TranslateService,
      private locale: LocaleService,
      private configService: RuntimeConfigService) {
    this.lang = locale.getLocale();
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
    this.currentUser = this.configService.getCurrentUser();
    if (this.currentUser.firstName === null && this.currentUser.lastName === null) {
      this.headerUserName = this.currentUser.username;
    } else {
      this.headerUserName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    }
  }

  ngOnInit() { }

  changeLang(language: string) {
    this.lang = this.changeLangService.changeLang(language);
  }

  public onToggleSidenav = () => {
    this.sidenavToggle.emit();
  }
}
