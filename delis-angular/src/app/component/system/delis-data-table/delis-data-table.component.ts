import { AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import { MatPaginator, MatSort} from "@angular/material";
import { Router} from "@angular/router";
import { tap} from "rxjs/operators";

import { SHOW_DATE_FORMAT } from "../../../app.constants";
import { DOCUMENT_PATH } from "../../../app.constants";
import { SEND_DOCUMENT_PATH } from "../../../app.constants";
import { IDENTIFIER_PATH } from "../../../app.constants";

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

@Component({
  selector: 'app-delis-data-table',
  templateUrl: './delis-data-table.component.html',
  styleUrls: ['./delis-data-table.component.scss']
})
export class DelisDataTableComponent implements OnInit, AfterViewInit {

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

  private filter: TableStateModel;

  allDisplayedColumns: Array<string> = new Array<string>();
  allDisplayedColumnsData: Array<HideColumnModel>;

  selectedDisplayedColumns: string[] = [];

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  breakpointCols: number;
  breakpointColspan: number;

  delisDataTableColumnModel: DelisDataTableColumnModel[];

  constructor(private router: Router, private daterangeObservable: DaterangeObservable, private refreshObservable: RefreshObservable) {
    this.daterangeObservable.listen().subscribe((range: Range) => {
      if (location.href.endsWith('/' + this.path)) {
        if (range.fromDate !== null && range.toDate !== null) {
          this.filter.dateRange = range;
        } else {
          this.filter.dateRange = null;
        }
        this.paginator.pageIndex = 0;
        this.loadPage();
      }
    });
    this.daterangeObservable.listen().subscribe((range: Range) => {
      if (location.href.endsWith('/' + this.path)) {
        this.refresh();
      }
    });
  }

  ngOnInit() {
    this.breakpointCols = (window.innerWidth <= 500) ? 1 : 8;
    this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
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
    this.dataSource.load(this.filter);
  }

  ngAfterViewInit() {
    this.paginator.page.pipe(tap(() => this.loadPage())).subscribe();
  }

  initData() {
    this.initDisplayedColumnsData();
    this.initFilter();
  }

  initDisplayedColumnsData() {
    this.allDisplayedColumns = this.delisDataTableColumnModel.map(value => { return value.displayedColumn});
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
    this.filter = this.stateService.getFilter();
    if (this.filter === undefined) {
      this.initDefaultFilter();
    } else {
      this.paginator.pageIndex = this.filter.pageIndex;
      this.paginator.pageSize = this.filter.pageSize;
      this.sort = this.filter.sort;
      for (const field in this.filter) {
        this.textFilterModel[field] = this.filter[field];
      }
    }
  }

  initDefaultFilter() {
    this.sort.active = 'createTime';
    this.sort.direction = 'desc';
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
    this.dataSource.load(this.filter);
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
    // window.location.replace(this.path)
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/' + this.path]));
  }

  refresh() {
    this.loadPage();
  }

  handleSortChange(event: any) {
    this.sort = event;
    this.paginator.pageIndex = 0;
    this.loadPage();
  }

  applyFilter(col: string, event: any, typeColumn: string) {
    this.initDefaultFilter();
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
