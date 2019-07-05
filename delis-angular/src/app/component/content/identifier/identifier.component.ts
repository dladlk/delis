import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { Router } from '@angular/router';
import { merge} from 'rxjs';
import { tap } from 'rxjs/operators';

import {SHOW_DATE_FORMAT} from '../../../app.constants';

import { IdentifierService } from '../../../service/content/identifier.service';
import { IdentifierFilterModel } from '../../../model/filter/identifier-filter.model';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { RefreshObservable } from '../../../observable/refresh.observable';
import { Range } from '../../system/date-range/model/model';
import { HideColumnModel } from "../../../model/content/hide-column.model";
import { IdentifierDataSource } from './identifier-data-source';

const BUNDLE_PREFIX = 'identifier.table.columnName.';

@Component({
  selector: 'app-identifier',
  templateUrl: './identifier.component.html',
  styleUrls: ['./identifier.component.scss']
})
export class IdentifierComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  private filter: IdentifierFilterModel;

  public dataSource: IdentifierDataSource;
  allDisplayedColumns: string[] = ['createTime', 'organisation', 'identifierGroup', 'type', 'value', 'uniqueValueType', 'status', 'name'];
  selectedDisplayedColumns: string[] = [];

  private pageIndex = 0;
  private pageSize = 10;

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  organizations: string[] = [];
  statusList: EnumInfoModel[];
  selectedStatusList: any;
  selectedOrganisation: any;

  allDisplayedColumnsData: Array<HideColumnModel>;

  breakpointCols: number;
  breakpointColspan: number;

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    private identifierService: IdentifierService,
    private daterangeObservable: DaterangeObservable,
    private refreshObservable: RefreshObservable) {
    this.daterangeObservable.listen().subscribe((range: Range) => {
      if (location.href.endsWith('/identifier')) {
        if (range.fromDate !== null && range.toDate !== null) {
          this.filter.dateRange = range;
        } else {
          this.filter.dateRange = null;
        }
        this.paginator.pageIndex = 0;
        this.loadPage();
      }
    });
    this.refreshObservable.listen().subscribe(() => {
      if (location.href.endsWith('/identifier')) {
        this.initSelected();
        this.loadPage();
      }
    });
    this.displayedColumnsDataInit();
  }

  ngOnInit() {
    this.breakpointCols = (window.innerWidth <= 500) ? 1 : 8;
    this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
    this.selectedDisplayedColumns = Object.assign([], this.allDisplayedColumns);
    this.initSelected();
    this.filter = new IdentifierFilterModel();
    this.dataSource = new IdentifierDataSource(this.identifierService);
    this.dataSource.load(0, 10, this.filter);
  }

  initSelected() {
    this.storage.select('Identifier', null).subscribe(enumInfo => {
      this.statusList = enumInfo.status;
      this.selectedStatusList = this.statusList[0];
    });
    this.storage.select('organizations', null).subscribe(organizationsInfo => {
      this.organizations = organizationsInfo;
      this.selectedOrganisation = this.organizations[0];
    });
  }

  displayedColumnsDataInit() {
    this.allDisplayedColumnsData = new Array<HideColumnModel>();
    for (const col of this.allDisplayedColumns) {
      let hcm: HideColumnModel = new HideColumnModel();
      hcm.columnName = col;
      hcm.columnBundle = BUNDLE_PREFIX + col;
      this.allDisplayedColumnsData.push(hcm);
    }
  }

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);
    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        tap(() => this.loadPage())
      )
      .subscribe();
  }

  loadPage() {
    if (this.paginator.pageSize !== this.pageSize) {
      this.paginator.pageIndex = 0;
    }
    this.filter.sortBy = 'orderBy_' + this.sort.active + '_' + this.sort.direction;
    this.dataSource.load(this.paginator.pageIndex, this.paginator.pageSize, this.filter);
    this.pageIndex = this.paginator.pageIndex;
    this.pageSize = this.paginator.pageSize;
  }

  onRowClicked(row) {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/identifier', row.id]));
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

  onResize(event) {
    this.breakpointCols = (event.target.innerWidth <= 500) ? 1 : 8;
    this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
  }

  clear() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/identifier']));
  }

  refresh() {
    this.loadPage();
  }

  applyFilter(col: string, event: any) {
    if (col === 'organisation') {
      if (event === 'All' || event === 'Alle') {
        this.filter[col] = 'ALL';
      } else {
        this.filter[col] = event;
      }
    } else {
      if (event.name === undefined) {
        this.filter[col] = event;
      } else {
        this.filter[col] = event.name;
      }
    }
    this.paginator.pageIndex = 0;
    this.loadPage();
  }
}
