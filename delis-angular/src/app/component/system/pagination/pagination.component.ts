import {Component, Input, OnInit} from '@angular/core';
import {PaginationModel} from '../../../model/system/pagination.model';
import {LocaleService} from '../../../service/system/locale.service';
import {TranslateService} from '@ngx-translate/core';
import {RefreshObservable} from '../../../observable/refresh.observable';
import {PaginationObservable} from '../../../observable/pagination.observable';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
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
    private refreshObservable: RefreshObservable,
    private translate: TranslateService,
    private locale: LocaleService,
    private paginationObservable: PaginationObservable) {
    this.translate.use(locale.getLocale().match(/en|da/) ? locale.getLocale() : 'en');
  }

  ngOnInit() {
  }

  loadPage(page: number) {
    this.pagination.currentPage = page;
    this.paginationObservable.loadPage(this.pagination);
  }

  loadPageSize() {
    this.pagination.currentPage = 1;
    this.pagination.pageSize = this.pagination.selectedPageSize.pageSize;
    this.paginationObservable.loadPageSize(this.pagination);
  }

  clearFilter() {
    this.pagination.selectedPageSize = {pageSize: 10};
    this.paginationObservable.clearFilter();
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
    this.refreshObservable.refreshPage();
  }
}
