import { Component, Input } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../../../../service/locale.service";

@Component({
    selector: 'table-header-sort',
    templateUrl: './table-header-sort.component.html',
    styleUrls: ['./table-header-sort.component.scss']
})
export class TableHeaderSortComponent {

    @Input() headerName: string;
    @Input() countClick: number;

    constructor(private translate: TranslateService, private locale: LocaleService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }
}
