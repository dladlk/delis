import { Component, OnInit, Input } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";

import { LocaleService } from "../../../service/locale.service";

@Component({
    selector: 'app-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.scss']
})
export class PageHeaderComponent implements OnInit {

    @Input() heading: string;
    @Input() icon: string;

    constructor(private translate: TranslateService, private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {}
}
