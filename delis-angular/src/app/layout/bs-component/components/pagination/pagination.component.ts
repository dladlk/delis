import { Component, Input } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../router.animations";
import { LocaleService } from "../../../../service/locale.service";
import { PaginationService } from "./pagination.service";
import { PaginationModel } from "./pagination.model";

@Component({
    selector: 'app-pagination',
    templateUrl: './pagination.component.html',
    styleUrls: ['./pagination.component.scss'],
    animations: [routerTransition()]
})
export class PaginationComponent {

    @Input() public pagination: PaginationModel;

    pageSizes = [
        {pageSize: 5},
        {pageSize: 10},
        {pageSize: 20},
        {pageSize: 50},
        {pageSize: 100}
    ];

    selectedPageSize: any;

    constructor(private translate: TranslateService, private locale: LocaleService, private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.selectedPageSize = {pageSize: 10, selected: true};
        this.pagination = new PaginationModel();
    }

    loadPage(page: number) {
        this.pagination.currentPage = page;
        this.paginationService.loadPage(this.pagination);
    }

    loadPageSize() {
        this.pagination.pageSize = this.selectedPageSize.pageSize;
        this.paginationService.loadPageSize(this.pagination);
    }
}
