import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../../router.animations';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../service/locale.service";

@Component({
  selector: 'app-publishing',
  templateUrl: './publishing.component.html',
  styleUrls: ['./publishing.component.scss'],
  animations: [routerTransition()]
})
export class PublishingComponent implements OnInit {

  constructor(private translate: TranslateService, private locale: LocaleService) {
      this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
  }

  ngOnInit() {}
}
