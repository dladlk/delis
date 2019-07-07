import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';

import { SHOW_DATE_FORMAT } from '../../../app.constants';

import { DocumentFilterModel } from '../../../model/filter/document-filter.model';
import { DocumentDataSource } from './document-data-source';
import { EnumInfoModel } from '../../../model/system/enum-info.model';
import { LocalStorageService } from '../../../service/system/local-storage.service';
import { DocumentService } from '../../../service/content/document.service';
import { DaterangeObservable } from '../../../observable/daterange.observable';
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
  receiverIdentifier: string;
  senderName: string;

  runSpinner = false;

  breakpointCols: number;
  breakpointColspan: number;

  lastHour = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private storage: LocalStorageService,
    private documentService: DocumentService,
    private daterangeObservable: DaterangeObservable) {
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

    this.route.queryParamMap.subscribe(params => {
      const queryParamMap = {...params.keys, ...params};
      let queryParams = queryParamMap['params'];
      if (queryParams.lastHour !== undefined) {
        if (queryParams.lastHour) {
          this.lastHour = true;
          for (let field of Object.getOwnPropertyNames(queryParams)) {
            if (this.filter.hasOwnProperty(field)) {
              let status = this.statuses.find(el => el.viewName === queryParams[field]);
              let documentType = this.documentTypes.find(el => el.viewName === queryParams[field]);
              let ingoingFormat = this.ingoingFormats.find(el => el.viewName === queryParams[field]);
              let organisation = this.organisations.find(el => el === queryParams[field]);
              this.filter[field] = queryParams[field];
              if (status !== undefined) {
                this.filter[field] = status.name;
                this.selectedStatus = status;
              }
              if (documentType !== undefined) {
                this.filter[field] = documentType.name;
                this.selectedDocumentType = documentType;
              }
              if (ingoingFormat !== undefined) {
                this.filter[field] = ingoingFormat.name;
                this.selectedIngoingFormats = ingoingFormat;
              }
              if (organisation !== undefined) {
                this.filter[field] = organisation;
                this.selectedOrganisation = organisation;
              }
              if (field === 'receiverIdentifier') {
                this.receiverIdentifier = queryParams[field];
              }
              if (field === 'senderName') {
                this.senderName = queryParams[field];
              }
            }
          }
        } else {
          this.lastHour = false;
        }
      }
    });

    this.dataSource.load(0, 10, this.filter, this.lastHour);
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
    this.dataSource.load(this.paginator.pageIndex, this.paginator.pageSize, this.filter, this.lastHour);
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
