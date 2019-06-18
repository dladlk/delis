import { Component, Input } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {ErrorModel} from '../../../../models/error.model';
import {LocaleService} from '../../../../service/locale.service';

@Component({
    selector: 'app-error',
    templateUrl: './error.component.html'
})
export class ErrorComponent {

    @Input() errorModel: ErrorModel;

    constructor( private locale: LocaleService, private translate: TranslateService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }
}
