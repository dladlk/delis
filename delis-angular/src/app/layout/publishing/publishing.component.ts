import { Component, OnInit } from '@angular/core';
import { routerTransition } from '../../router.animations';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-publishing',
  templateUrl: './publishing.component.html',
  styleUrls: ['./publishing.component.scss'],
  animations: [routerTransition()]
})
export class PublishingComponent implements OnInit {

  constructor(private translate: TranslateService) {
      const browserLang = this.translate.getBrowserLang();
      this.translate.use(browserLang.match(/en|da/) ? browserLang : 'en');
  }

  ngOnInit() {}
}
