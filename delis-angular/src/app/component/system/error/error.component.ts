import {Component, Input, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {LocaleService} from '../../../service/system/locale.service';
import {ErrorModel} from '../../../model/system/error.model';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {

  @Input() errorModel: ErrorModel;

  constructor( private locale: LocaleService, private translate: TranslateService) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
  }

  ngOnInit() {
  }

}
