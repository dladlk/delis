import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { LocaleService } from '../../../service/system/locale.service';
import { ChangeLangService } from '../../../service/system/change-lang.service';

@Component({
  selector: 'app-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.scss']
})
export class SidenavListComponent implements OnInit {

  @Output() sidenavClose = new EventEmitter();

  public lang: string;

  constructor(private locale: LocaleService, private changeLangService: ChangeLangService) {
    this.lang = locale.getLocale();
  }

  ngOnInit() {
  }

  public onSidenavClose = (event: any) => {
    if (event !== null) {
      this.lang = this.changeLangService.changeLang(event);
    }
    this.sidenavClose.emit();
  }
}
