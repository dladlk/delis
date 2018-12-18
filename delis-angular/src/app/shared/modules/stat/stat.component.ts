import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";

@Component({
    selector: 'app-stat',
    templateUrl: './stat.component.html',
    styleUrls: ['./stat.component.scss']
})
export class StatComponent implements OnInit {
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: string;
    @Input() label: string;
    @Input() data: number;
    @Input() router: RouterLink;
    @Output() event: EventEmitter<any> = new EventEmitter();

    constructor(private translate: TranslateService) {
        const browserLang = this.translate.getBrowserLang();
        this.translate.use(browserLang.match(/en|da/) ? browserLang : 'en');
    }

    ngOnInit() {}
}
