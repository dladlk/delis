import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

import { SHOW_DATE_FORMAT } from '../../../app.constants';
import { CurrentUserModel } from '../../../model/system/current-user.model';
import { RuntimeConfigService } from '../../../service/system/runtime-config.service';
import { ChangeLangService } from '../../../service/system/change-lang.service';
import { LocaleService } from '../../../service/system/locale.service';

@Component({
  selector: 'app-topnav',
  templateUrl: './topnav.component.html',
  styleUrls: ['./topnav.component.scss']
})
export class TopnavComponent implements OnInit {

  public pushRightClass: string;
  public lang: string;
  public currentUser: CurrentUserModel;
  public headerUserName: string;

  @Output() public sidenavToggle = new EventEmitter();

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  constructor(
      private router: Router,
      private changeLangService: ChangeLangService,
      private configService: RuntimeConfigService,
      private locale: LocaleService) {
    this.router.events.subscribe(val => {
      if (val instanceof NavigationEnd && window.innerWidth <= 992 && this.isToggled()) {
        this.toggleSidebar();
      }
    });
    this.currentUser = this.configService.getCurrentUser();
    if (this.currentUser.firstName === null && this.currentUser.lastName === null) {
      this.headerUserName = this.currentUser.username;
    } else {
      this.headerUserName = this.currentUser.firstName + ' ' + this.currentUser.lastName;
    }
    this.lang = locale.getLocale();
  }

  ngOnInit() {
    this.pushRightClass = 'push-right';
  }

  isToggled(): boolean {
    const dom: Element = document.querySelector('body');
    return dom.classList.contains(this.pushRightClass);
  }

  toggleSidebar() {
    const dom: any = document.querySelector('body');
    dom.classList.toggle(this.pushRightClass);
  }

  public onToggleSidenav = () => {
    this.sidenavToggle.emit();
  }

  onLoggedout() {
    this.router.navigate(['/logout']);
  }

  changeLang(language: string) {
    this.lang = this.changeLangService.changeLang(language);
  }

}
