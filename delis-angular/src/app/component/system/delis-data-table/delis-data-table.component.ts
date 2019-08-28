import { AfterViewInit, Component, Input, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatPaginator, MatSort } from "@angular/material";
import { ActivatedRoute, Router } from "@angular/router";
import { Subscription } from 'rxjs';
import { tap } from "rxjs/operators";
import * as moment from 'moment';

import {
    DOCUMENT_PATH,
    IDENTIFIER_PATH,
    SEND_DOCUMENT_PATH,
    SHOW_DATE_FORMAT,
    LAST_ACTIVE_MAT_ROW,
    CHART_DATE_FORMAT_START,
    CHART_DATE_FORMAT_END
} from "../../../app.constants";

import { DelisDataTableColumnModel } from "../../../model/content/delis-data-table-column.model";
import { TableStateModel } from "../../../model/filter/table-state.model";
import { HideColumnModel } from "../../../model/content/hide-column.model";
import { AbstractEntityModel } from "../../../model/content/abstract-entity.model";
import { SendDocumentFilterModel } from "../../../model/filter/send-document-filter.model";
import { DocumentFilterModel } from "../../../model/filter/document-filter.model";
import { IdentifierFilterModel } from "../../../model/filter/identifier-filter.model";
import { Range } from '../date-range/model/model';
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
import { RedirectContentService } from "../../../service/content/redirect-content.service";

const DOCUMENT_STATUS = 'documentStatus';
const DATE_PATTERN = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/;

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
    // statusError: boolean;
    lastVisitedId: number;

    LAST_ACTIVE_MAT_ROW = LAST_ACTIVE_MAT_ROW;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private documentErrorService: DocumentErrorService,
        private daterangeObservable: DaterangeObservable,
        private redirectService: RedirectContentService,
        private refreshObservable: RefreshObservable,
        private resetDaterangeObservable: ResetDaterangeObservable) {
        this.rangeUpdate$ = this.daterangeObservable.listen().subscribe((range: Range) => {
            if (range.fromDate !== null && range.toDate !== null) {
                this.filter.dateRange = range;
            } else {
                this.filter.dateRange = null;
            }
            this.paginator.pageIndex = 0;
            this.loadPage();
        });
        this.refreshUpdate$ = this.refreshObservable.listen().subscribe(() => this.refresh());
    }

    ngOnInit() {

        this.statusErrors = this.documentErrorService.statusErrors;
        this.route.queryParamMap.subscribe(params => {
            const queryParamMap = {...params.keys, ...params};
            let queryParams = queryParamMap['params'];
            if (queryParams.skip !== undefined) {
                this.skip = JSON.parse(queryParams.skip);
            } else {
                this.skip = true;
            }
        });

        // this.breakpointCols = (window.innerWidth <= 500) ? 1 : 8;
        // this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
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
    }

    ngOnDestroy() {
        if (this.rangeUpdate$) {
          this.rangeUpdate$.unsubscribe();
        }
        if (this.refreshUpdate$) {
            this.refreshUpdate$.unsubscribe();
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
    }

    initDefaultFilter() {
        this.sort.active = 'createTime';
        this.sort.direction = 'desc';
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

        let redirectData = this.redirectService.redirectData;
        if (redirectData) {
            if (redirectData.path === this.path) {
                if (redirectData.dateStart !== null && redirectData.dateEnd !== null) {
                    let fromDate;
                    let toDate;
                    let regexConst = new RegExp(DATE_PATTERN);
                    if (regexConst.test(redirectData.dateStart)) {
                        let start = moment(redirectData.dateStart).format(CHART_DATE_FORMAT_START);
                        let end = moment(redirectData.dateEnd).format(CHART_DATE_FORMAT_END);
                        fromDate = new Date(start);
                        toDate = new Date(end);
                    } else {
                        fromDate = new Date(redirectData.dateStart);
                        toDate = new Date(redirectData.dateEnd);
                    }
                    this.filter.dateRange = { fromDate: fromDate, toDate: toDate };
                } else {
                    this.filter.dateRange = null;
                }
                if (redirectData.path === DOCUMENT_PATH) {
                    if (redirectData.statusError) {
                        this.enumFilterModel[DOCUMENT_STATUS].value = this.enumFilterModel[DOCUMENT_STATUS].list[1];
                        this.filter[DOCUMENT_STATUS] = this.enumFilterModel[DOCUMENT_STATUS].value.name;
                    }
                }
            } else {
                this.redirectService.resetRedirectData();
            }
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
        this.redirectService.resetRedirectData();
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
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
            this.router.navigate(['/' + this.path]));
    }

    refresh() {
        this.loadPage();
    }

    handleSortChange(event: any) {
        this.sort.direction = event.direction;
        this.sort.active = event.active;
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
