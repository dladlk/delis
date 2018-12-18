import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../service/locale.service";

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

    constructor(private translate: TranslateService, private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit() {}
}
