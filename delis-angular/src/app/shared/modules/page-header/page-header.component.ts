import { Component, OnInit, Input } from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Component({
    selector: 'app-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.scss']
})
export class PageHeaderComponent implements OnInit {

    @Input() heading: string;
    @Input() icon: string;

    constructor(private translate: TranslateService) {
        const browserLang = this.translate.getBrowserLang();
        this.translate.use(browserLang.match(/en|da/) ? browserLang : 'en');
    }

    ngOnInit() {}
}
