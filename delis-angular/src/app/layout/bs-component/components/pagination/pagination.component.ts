import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { routerTransition } from '../../../../router.animations';
import { LocaleService } from '../../../../service/locale.service';
import { PaginationService } from './pagination.service';
import { PaginationModel } from './pagination.model';
import { RefreshService } from "../../../../service/refresh.service";

@Component({
    selector: 'app-pagination',
    templateUrl: './pagination.component.html',
    styleUrls: ['./pagination.component.scss'],
    animations: [routerTransition()]
})
export class PaginationComponent implements OnInit {

    @Input() public pagination: PaginationModel;
    @Input() public dropdownPosition: string;

    pageSizes = [
        {pageSize: 5},
        {pageSize: 10},
        {pageSize: 20},
        {pageSize: 50},
        {pageSize: 100}
    ];

    constructor(
        private refresh: RefreshService,
        private translate: TranslateService,
        private locale: LocaleService,
        private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
    }

    ngOnInit(): void {

    }

    loadPage(page: number) {
        this.pagination.currentPage = page;
        this.paginationService.loadPage(this.pagination);
    }

    loadPageSize() {
        this.pagination.currentPage = 1;
        this.pagination.pageSize = this.pagination.selectedPageSize.pageSize;
        this.paginationService.loadPageSize(this.pagination);
    }

    clearFilter() {
        this.pagination.selectedPageSize = {pageSize: 10};
        this.paginationService.clearFilter();
    }

    generateFrom(): number {
        if (this.pagination.collectionSize === 0) {
            return 0;
        } else if (this.pagination.pageSize > this.pagination.collectionSize) {
            return 1;
        } else {
            return this.pagination.pageSize * (this.pagination.currentPage - 1) + 1;
        }
    }

    generateTo() {
        const lastSize = this.pagination.pageSize * (this.pagination.currentPage - 1) + this.pagination.pageSize;
        if (lastSize < this.pagination.collectionSize) {
            return lastSize;
        } else {
            return this.pagination.collectionSize;
        }
    }

    refreshData() {
        this.refresh.refreshPage();
    }
}
