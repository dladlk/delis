import { AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from "@angular/material";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { tap } from "rxjs/operators";
import * as moment from 'moment';

import { DOCUMENT_PATH, IDENTIFIER_PATH, LAST_ACTIVE_MAT_ROW, SEND_DOCUMENT_PATH, SHOW_DATE_FORMAT } from "../../../app.constants";

import { DelisDataTableColumnModel } from "../../../model/content/delis-data-table-column.model";
import { TableStateModel } from "../../../model/filter/table-state.model";
import { HideColumnModel } from "../../../model/content/hide-column.model";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { SendDocumentFilterModel } from "../../../model/filter/send-document-filter.model";
import { DocumentFilterModel } from "../../../model/filter/document-filter.model";
import { IdentifierFilterModel } from "../../../model/filter/identifier-filter.model";
import { Range, RangeUpdate } from '../date-range/model/model';
import { StateService } from "../../../service/state/state-service";
import { DelisService } from "../../../service/content/delis-service";
import { DelisDataSource } from "../../content/delis-data-source";
import { DocumentDataSource } from "../../content/document/document-data-source";
import { IdentifierDataSource } from "../../content/identifier/identifier-data-source";
import { SendDocumentDataSource } from "../../content/send-document/send-document-data-source";
import { DataTableConfig } from "../../content/data-table-config";
import { DaterangeObservable } from "../../../observable/daterange.observable";
import { RefreshObservable } from "../../../observable/refresh.observable";
import { ResetDaterangeObservable } from "../../../observable/reset-daterange.observable";
import { DocumentErrorService } from "../../content/document/document-error.service";
import { RangeStoreService } from "../date-range/service/range-store.service";
import { RoutingStateService } from "../../../service/state/routing-state.service";

@Component({
    selector: 'app-delis-data-table',
    templateUrl: './delis-data-table.component.html',
    styleUrls: ['./delis-data-table.component.scss']
})
export class DelisDataTableComponent implements OnInit, AfterViewInit, OnDestroy {

    @Input() header: string;
    @Input() path: string;
    @Input() BUNDLE_PREFIX: string;
    @Input() dataSource: DelisDataSource<AbstractEntityModel, TableStateModel>;
    @Input() enumFilterModel: any;
    @Input() textFilterModel: any;
    @Input() stateService: StateService<TableStateModel>;
    @Input() delisService: DelisService<AbstractEntityModel, TableStateModel>;

    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
    @ViewChild(MatSort, {static: true}) sort: MatSort;

    private rangeUpdate$: Subscription;
    private refreshUpdate$: Subscription;
    private queryParamMapSubscription$: Subscription;
    private filter: TableStateModel;

    statusErrors: string[] = [];

    allDisplayedColumns: Array<string> = new Array<string>();
    allDisplayedColumnsData: Array<HideColumnModel>;

    selectedDisplayedColumns: string[] = [];

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    breakpointCols: number;
    breakpointColspan: number;

    delisDataTableColumnModel: DelisDataTableColumnModel[];

    skip: boolean;
    statusError: boolean;
    lastVisitedId: number;
    dateStart: Date;
    dateEnd: Date;
    range: Range = null;

    LAST_ACTIVE_MAT_ROW = LAST_ACTIVE_MAT_ROW;

    previousRoute: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private location: Location,
        private documentErrorService: DocumentErrorService,
        private daterangeObservable: DaterangeObservable,
        private refreshObservable: RefreshObservable,
        private resetDaterangeObservable: ResetDaterangeObservable,
        private rangeStoreService: RangeStoreService,
        private routingState: RoutingStateService) { }

    ngOnInit() {
        this.previousRoute = this.routingState.getPreviousUrl();
        this.rangeUpdate$ = this.daterangeObservable.listen().subscribe((range: Range) => {
            this.paginator.pageIndex = 0;
            if (range.fromDate !== null && range.toDate !== null) {
                this.filter.dateRange = range;
                const start = moment(range.fromDate).format('YYYY-MM-DD');
                this.dateStart = new Date(start);
                const end = moment(range.toDate).format('YYYY-MM-DD');
                this.dateEnd = new Date(end);
                const queryParams: Params = { dateStart: start, dateEnd: end };
                this.router.navigate(
                    ['/' + this.path],
                    {
                        relativeTo: this.route,
                        queryParams: queryParams,
                        queryParamsHandling: 'merge'
                    });
            } else {
                this.filter.dateRange = null;
            }
        });
        this.refreshUpdate$ = this.refreshObservable.listen().subscribe(() => this.refresh());
        this.obtainQueryParamsAndFetchData();
        // this.breakpointCols = (window.innerWidth <= 500) ? 1 : 8;
        // this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
    }

    obtainQueryParamsAndFetchData() {
        this.queryParamMapSubscription$ = this.route.queryParamMap.subscribe(params => {
            this.statusErrors = this.documentErrorService.statusErrors;
            this.statusError = false;
            const queryParamMap = {...params.keys, ...params};
            let queryParams = queryParamMap['params'];
            if (queryParams.skip !== undefined) {
                this.skip = JSON.parse(queryParams.skip);
            } else {
                this.skip = true;
            }
            if (queryParams.statusError !== undefined) {
                this.statusError = JSON.parse(queryParams.statusError);
            } else {
                this.statusError = false;
            }
            if (queryParams.dateStart !== undefined) {
                const start = moment(queryParams.dateStart).format('YYYY-MM-DD 00:00:01');
                this.dateStart = new Date(start);
            } else {
                this.dateStart = null;
            }
            if (queryParams.dateEnd !== undefined) {
                const end = moment(queryParams.dateEnd).format('YYYY-MM-DD 23:59:59');
                this.dateEnd = new Date(end);
            } else {
                this.dateEnd = null;
            }
            if (this.dateStart !== null && this.dateEnd !== null) {
                const rangeUpdate = new RangeUpdate();
                rangeUpdate.range = {fromDate: this.dateStart, toDate: this.dateEnd};
                rangeUpdate.update = false;
                this.range = rangeUpdate.range;
                this.rangeStoreService.updateRange(rangeUpdate);
            }
            if (this.path === SEND_DOCUMENT_PATH) {
                this.dataSource = new SendDocumentDataSource(this.delisService);
                this.delisDataTableColumnModel = DataTableConfig.INIT_SEND_DOCUMENT_COLUMNS_CONFIG();
            }
            if (this.path === DOCUMENT_PATH) {
                this.dataSource = new DocumentDataSource(this.delisService);
                this.delisDataTableColumnModel = DataTableConfig.INIT_DOCUMENT_COLUMNS_CONFIG();
            }
            if (this.path === IDENTIFIER_PATH) {
                this.dataSource = new IdentifierDataSource(this.delisService);
                this.delisDataTableColumnModel = DataTableConfig.INIT_IDENTIFIER_COLUMNS_CONFIG();
            }
            this.initData();
            this.dataSource.load(this.stateService);
        });
    }

    ngOnDestroy() {
        if (this.rangeUpdate$) {
            this.rangeUpdate$.unsubscribe();
        }
        if (this.refreshUpdate$) {
            this.refreshUpdate$.unsubscribe();
        }
        if (this.queryParamMapSubscription$) {
            this.queryParamMapSubscription$.unsubscribe();
        }
    }

    ngAfterViewInit() {
        this.paginator.page.pipe(tap(() => this.loadPage())).subscribe();
    }

    initData() {
        if (!this.paginator.pageSize) {
            this.paginator.pageSize = 10;
        }
        this.initDisplayedColumnsData();
        this.initFilter();
    }

    initDisplayedColumnsData() {
        this.allDisplayedColumns = this.delisDataTableColumnModel.map(value => {
            return value.displayedColumn
        });
        this.allDisplayedColumnsData = new Array<HideColumnModel>();
        for (const col of this.allDisplayedColumns) {
            let hcm: HideColumnModel = new HideColumnModel();
            hcm.columnName = col;
            hcm.columnBundle = this.BUNDLE_PREFIX + col;
            this.allDisplayedColumnsData.push(hcm);
        }
        this.selectedDisplayedColumns = Object.assign([], this.allDisplayedColumns);
    }

    initFilter() {
        if (this.skip) {
            this.initDefaultFilter();
        } else {
            this.filter = this.stateService.getFilter();
            if (this.filter === undefined) {
                this.initDefaultFilter();
            } else {
                this.paginator.pageIndex = this.filter.pageIndex;
                this.paginator.pageSize = this.filter.pageSize;
                this.sort = this.filter.sort;
                this.lastVisitedId = this.filter.detailsState.currentId;
                for (const field in this.filter) {
                    if (!this.filter.hasOwnProperty(field)) {
                        continue;
                    }
                    this.textFilterModel[field] = this.filter[field];
                    if (this.enumFilterModel[field] !== undefined) {
                        if (this.enumFilterModel[field].value.name === undefined) {
                            if (this.filter[field] === 'ALL') {
                                this.enumFilterModel[field].value = this.enumFilterModel[field].list[0];
                            } else {
                                this.enumFilterModel[field].value = this.filter[field];
                            }
                        } else {
                            this.enumFilterModel[field].value = this.enumFilterModel[field].list.find(value => value.name === this.filter[field]);
                        }
                    }
                }
            }
        }
        if (this.range !== null) {
            this.filter.dateRange = this.range;
        }
        this.filter.statusError = this.statusError;
    }

    initDefaultFilter() {
        this.sort.active = 'createTime'; // TODO https://github.com/angular/components/issues/12754#issuecomment-419461502
        this.sort.direction = 'desc'; // TODO It looks like the sort header UI only updates when interacted with via user interaction. If the update comes from either a property binding or from a method call it updates the state, but the UI is not reflected.
        this.paginator.pageIndex = 0;
        this.paginator.pageSize = 10;
        if (this.path === SEND_DOCUMENT_PATH) {
            this.filter = new SendDocumentFilterModel(this.sort);
        }
        if (this.path === DOCUMENT_PATH) {
            this.filter = new DocumentFilterModel(this.sort);
        }
        if (this.path === IDENTIFIER_PATH) {
            this.filter = new IdentifierFilterModel(this.sort);
        }
        this.stateService.setFilter(this.filter);
    }

    loadPage() {
        if (this.paginator.pageSize !== this.filter.pageSize) {
            this.paginator.pageIndex = 0;
        }
        this.filter.sort = this.sort;
        this.filter.pageSize = this.paginator.pageSize;
        this.filter.pageIndex = this.paginator.pageIndex;
        this.stateService.setFilter(this.filter);
        this.dataSource.load(this.stateService);
    }

    onRowClicked(row) {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/' + this.path, row.id]));
    }

    onResize(event) {
        this.breakpointCols = (event.target.innerWidth <= 500) ? 1 : 8;
        this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
    }

    showHideColumns(columns: []) {
        this.selectedDisplayedColumns = Object.assign([], this.allDisplayedColumns);
        for (const col of columns) {
            const indexSelectedDisplayedColumns: number = this.selectedDisplayedColumns.indexOf(col, 0);
            if (indexSelectedDisplayedColumns > -1) {
                this.selectedDisplayedColumns.splice(indexSelectedDisplayedColumns, 1);
            }
        }
    }

    clear() {
        this.initDefaultFilter();
        for (const filterParam in this.enumFilterModel) {
            if (!this.enumFilterModel.hasOwnProperty(filterParam)) {
                continue;
            }
            this.enumFilterModel[filterParam].value = this.enumFilterModel[filterParam].list[0];
        }
        for (const filterParam in this.textFilterModel) {
            if (!this.textFilterModel.hasOwnProperty(filterParam)) {
                continue;
            }
            this.textFilterModel[filterParam] = null;
        }
        this.resetDaterangeObservable.reset();
        if (this.router.url.indexOf("?") >= 0) {
            let url: string = this.router.url.substring(0, this.router.url.indexOf("?"));
            this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
                this.router.navigate([url]));
        }
    }

    refresh() {
        this.loadPage();
    }

    back() {
        this.location.back();
    }

    handleSortChange(event: any) {
        this.sort = event;
        this.paginator.pageIndex = 0;
        this.loadPage();
    }

    applyFilter(col: string, event: any, typeColumn: string) {
        if (typeColumn === 'enumString') {
            if (event === 'All' || event === 'Alle') {
                this.filter[col] = 'ALL';
            } else {
                this.filter[col] = event;
            }
        }
        if (typeColumn === 'enumInfo') {
            this.filter[col] = event.name;
        }
        if (typeColumn === 'text') {
            this.filter[col] = event;
        }
        this.paginator.pageIndex = 0;
        this.loadPage();
    }
}
