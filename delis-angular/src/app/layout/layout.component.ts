import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { LocaleService } from "../service/locale.service";
import { ListenErrorService } from "../service/listen.error.service";
import { ErrorModel } from "../models/error.model";

@Component({
    selector: 'app-layout',
    templateUrl: './layout.component.html',
    styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

    collapsedSideBar: boolean;
    error = false;
    listenError: ErrorModel;

    constructor(private translate: TranslateService, private locale: LocaleService, private errorService: ListenErrorService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.errorService.listen().subscribe((listenError: ErrorModel) => {
            this.error = true;
            this.listenError = listenError;
        });
        this.error = false;
    }

    ngOnInit() {}

    receiveCollapsed($event) {
        this.collapsedSideBar = $event;
    }
}
