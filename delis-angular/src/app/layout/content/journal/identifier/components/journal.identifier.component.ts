import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { PaginationModel } from "../../../../bs-component/components/pagination/pagination.model";
import { TableHeaderSortModel } from "../../../../bs-component/components/table-header-sort/table.header.sort.model";
import { JournalIdentifierFilterProcessResultModel } from "../models/journal.identifier.filter.process.result.model";
import { JournalIdentifierModel } from "../models/journal.identifier.model";
import { LocaleService } from "../../../../../service/locale.service";
import { PaginationService } from "../../../../bs-component/components/pagination/pagination.service";
import { JournalIdentifierService } from "../services/journal.identifier.service";
import { DateRangeModel } from "../../../../../models/date.range.model";
import { ErrorService } from "../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";
import { DaterangeService } from "../../../../bs-component/components/daterange/daterange.service";
import { DaterangeShowService } from "../../../../bs-component/components/daterange/daterange.show.service";

const COLUMN_NAME_ORGANIZATION = 'journal.identifier.table.columnName.organisation';
const COLUMN_NAME_IDENTIFIER = 'journal.identifier.table.columnName.identifier';
const COLUMN_NAME_MESSAGE = 'journal.identifier.table.columnName.message';
const COLUMN_NAME_DURATIOM_MS = 'journal.identifier.table.columnName.durationMs';
const COLUMN_NAME_CREATE_TIME = 'journal.identifier.table.columnName.createTime';

@Component({
    selector: 'app-journal-identifier',
    templateUrl: './journal.identifier.component.html',
    styleUrls: ['./journal.identifier.component.scss'],
    animations: [routerTransition()]
})
export class JournalIdentifierComponent implements OnInit {

    clearableSelect = false;

    pagination: PaginationModel;
    filter: JournalIdentifierFilterProcessResultModel;
    journalIdentifiers: JournalIdentifierModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];

    textIdentifier: string;
    textMessage: string;
    textDurationMs: string;

    organizations: [];
    selectedOrganization: any;

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    constructor(
        private journalIdentifierService: JournalIdentifierService,
        private translate: TranslateService,
        private locale: LocaleService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService,
        private errorService: ErrorService,
        private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize !== 0) {
                this.loadPage(pag.currentPage, pag.pageSize);
                this.pagination = pag;
            } else {
                this.initDefaultValues();
                this.clearAllFilter();
                this.loadPage(pag.currentPage, pag.pageSize);
            }
        });
        this.dtService.listen().subscribe((dtRange: DateRangeModel) => {
            if (dtRange.dateStart !== null && dtRange.dateEnd !== null) {
                this.filter.dateRange = dtRange;
            } else {
                this.filter.dateRange = null;
            }
            this.pagination.currentPage = 1;
            this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
        });
        this.dtShowService.listen().subscribe((show: boolean) => {
            this.filter.dateRange = null;
            this.loadPage(1, this.pagination.pageSize);
        });
    }

    ngOnInit(): void {
        this.initProcess();
        this.initSelected();
    }

    private initProcess() {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        this.currentProdJournalIdentifier(1, 10);
        this.clearAllFilter();
    }

    initSelected() {
        this.organizations = JSON.parse(localStorage.getItem("organizations"));
    }

    private initDefaultValues() {
        this.selectedOrganization = "ALL";
        this.filter = new JournalIdentifierFilterProcessResultModel();
        if (this.tableHeaderSortModels.length == 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_IDENTIFIER, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_MESSAGE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_DURATIOM_MS, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_CREATE_TIME, columnClick: 0
                }
            );
        }
    }

    clickFilter(target: string) {
        this.clickProcess(target);
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadOrganisations() {
        if (this.selectedOrganization === null) {
            this.selectedOrganization = 'ALL';
        }
        this.filter.organisation = this.selectedOrganization;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextIdentifier(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.identifier = null;
        } else {
            this.filter.identifier = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextMessage(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.message = null;
        } else {
            this.filter.message = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextDurationMs(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.durationMs = null;
        } else {
            this.filter.durationMs = parseInt(text);
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    clickProcess(columnName: string) {
        let countClick = this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick;
        countClick++;
        let columnEntity = columnName.split('.').reduce((first, last) => last);
        if (countClick === 1) {
            this.filter.sortBy = 'orderBy_' + columnEntity + '_Asc';
        }
        if (countClick === 2) {
            this.filter.sortBy = 'orderBy_' + columnEntity + '_Desc';
        }
        if (countClick > 2) {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = 0;
            this.filter.sortBy = 'orderBy_Id_Desc';
        } else {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = countClick;
        }
        this.clearFilter(columnName);
    }

    private loadPage(page: number, pageSize: number) {
        this.currentProdJournalIdentifier(page, pageSize);
    }

    private currentProdJournalIdentifier(currentPage: number, sizeElement: number) {
        this.journalIdentifierService.getListJournalIdentifiers(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.journalIdentifiers = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.selectedOrganization = 'ALL';
        this.textIdentifier = '';
        this.textMessage = '';
        this.textDurationMs = '';
        this.filter.dateRange = null;
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
    }
}
