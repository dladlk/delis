import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { Router } from '@angular/router';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';

import { SHOW_DATE_FORMAT } from '../../../app.constants';

import { DocumentFilterModel } from '../../../model/filter/document-filter.model';
import { DocumentDataSource } from './document-data-source';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { DocumentService } from '../../../service/content/document.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
import { RefreshObservable} from '../../../observable/refresh.observable';
import { Range } from '../../system/date-range/model/model';
import { HideColumnModel } from "../../../model/content/hide-column.model";

const BUNDLE_PREFIX = 'documents.table.columnName.';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit, AfterViewInit {

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  private filter: DocumentFilterModel;
  public dataSource: DocumentDataSource;

  allDisplayedColumns: string[] = [
    'createTime',
    'organisation',
    'receiverIdentifier',
    'documentStatus',
    'documentType',
    'ingoingDocumentFormat',
    'senderName'];
  selectedDisplayedColumns: string[] = [];

  allDisplayedColumnsData: Array<HideColumnModel>;

  private pageIndex = 0;
  private pageSize = 10;

  SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

  statuses: EnumInfoModel[] = [];
  documentTypes: EnumInfoModel[] = [];
  ingoingFormats: EnumInfoModel[] = [];
  lastErrors: EnumInfoModel[] = [];
  organisations: string[] = [];

  selectedStatus: any;
  selectedDocumentType: any;
  selectedIngoingFormats: any;
  selectedLastError: any;
  selectedOrganisation: any;

  runSpinner = false;

  breakpointCols: number;
  breakpointColspan: number;

  constructor(
    private router: Router,
    private storage: LocalStorageService,
    private documentService: DocumentService,
    private daterangeObservable: DaterangeObservable,
    private refreshObservable: RefreshObservable) {
    this.daterangeObservable.listen().subscribe((range: Range) => {
      if (location.href.endsWith('/document')) {
        if (range.fromDate !== null && range.toDate !== null) {
          this.filter.createTime = range;
        } else {
          this.filter.createTime = null;
        }
        this.paginator.pageIndex = 0;
        this.loadPage();
      }
    });
    this.refreshObservable.listen().subscribe(() => {
      if (location.href.endsWith('/document')) {
        this.initSelected();
        this.loadPage();
      }
    });
    this.displayedColumnsDataInit();
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

  ngOnInit() {
    this.breakpointCols = (window.innerWidth <= 500) ? 1 : 8;
    this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
    this.selectedDisplayedColumns = Object.assign([], this.allDisplayedColumns);
    this.initSelected();
    this.filter = new DocumentFilterModel();
    this.dataSource = new DocumentDataSource(this.documentService);
    this.dataSource.load(0, 10, this.filter);
  }

  initSelected() {
    this.storage.select('Document', null).subscribe(enumInfo => {
      this.statuses = enumInfo.documentStatus;
      this.documentTypes = enumInfo.documentType;
      this.ingoingFormats = enumInfo.ingoingDocumentFormat;
      this.lastErrors = enumInfo.lastError;
      this.selectedStatus = this.statuses[0];
      this.selectedDocumentType = this.documentTypes[0];
      this.selectedIngoingFormats = this.ingoingFormats[0];
      this.selectedLastError = this.lastErrors[0];
    });
    this.storage.select('organizations', null).subscribe(organisationsInfo => {
      this.organisations = organisationsInfo;
      this.selectedOrganisation = this.organisations[0];
    });
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

  showHideColumns(columns: []) {
    this.selectedDisplayedColumns = Object.assign([], this.allDisplayedColumns);
    for (const col of columns) {
      const indexSelectedDisplayedColumns: number = this.selectedDisplayedColumns.indexOf(col, 0);
      if (indexSelectedDisplayedColumns > -1) {
        this.selectedDisplayedColumns.splice(indexSelectedDisplayedColumns, 1);
      }
    }
  }

  onRowClicked(row) {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
        this.router.navigate(['/document', row.id]));
  }

  clear() {
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() =>
      this.router.navigate(['/document']));
  }

  refresh() {
    this.loadPage();
  }

  onResize(event) {
    this.breakpointCols = (event.target.innerWidth <= 500) ? 1 : 8;
    this.breakpointColspan = (window.innerWidth <= 500) ? 1 : 3;
  }

  applyFilter(col: string, event: any) {
    if (col === 'organisation') {
      if (event === 'All' || event === 'Alle') {
        this.filter[col] = 'ALL';
      } else {
        this.filter[col] = event;
      }
    } else {
      this.filter[col] = event.name;
    }
    this.paginator.pageIndex = 0;
    this.loadPage();
  }
}
