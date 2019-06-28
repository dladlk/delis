import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LocaleService } from '../../../service/system/locale.service';

@Component({
  selector: 'app-table-header-sort',
  templateUrl: './table-header-sort.component.html',
  styleUrls: ['./table-header-sort.component.scss']
})
export class TableHeaderSortComponent implements OnInit {

  @Input() headerName: string;
  @Input() countClick: number;

  constructor(private translate: TranslateService, private locale: LocaleService) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
  }

  ngOnInit() {
  }

}
