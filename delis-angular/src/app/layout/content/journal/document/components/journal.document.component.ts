import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { PaginationModel } from "../../../../bs-component/components/pagination/pagination.model";
import { LocaleService } from "../../../../../service/locale.service";
import { PaginationService } from "../../../../bs-component/components/pagination/pagination.service";
import { JournalDocumentService } from "../services/journal.document.service";
import { TableHeaderSortModel } from "../../../../bs-component/components/table-header-sort/table.header.sort.model";
import { JournalDocumentModel } from "../models/journal.document.model";
import { JournalDocumentFilterProcessResult } from "../models/journal.document.filter.process.result";
import { successList } from "../models/journal.document.view.model";
import { DateRangeModel } from "../../../../../models/date.range.model";
import { ErrorService } from "../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";

const COLUMN_NAME_ORGANIZATION = 'journal.documents.table.columnName.Organisation';
const COLUMN_NAME_SUCCESS = 'journal.documents.table.columnName.Success';
const COLUMN_NAME_TYPE = 'journal.documents.table.columnName.Type';
const COLUMN_NAME_MESSAGE = 'journal.documents.table.columnName.Message';
const COLUMN_NAME_DURATIOM_MS = 'journal.documents.table.columnName.DurationMs';
const COLUMN_NAME_CREATE_TIME = 'journal.documents.table.columnName.CreateTime';

@Component({
    selector: 'app-journal-documents',
    templateUrl: './journal.document.component.html',
    styleUrls: ['./journal.document.component.scss'],
    animations: [routerTransition()]
})
export class JournalDocumentComponent implements OnInit {

    pagination: PaginationModel;
    filter: JournalDocumentFilterProcessResult;
    journalDocuments: JournalDocumentModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    typeList: [];
    organizations: [];
    successList = successList;

    textDocument: string;
    textMessage: string;
    textDurationMs: string;
    selectedType: any;
    selectedSuccess: any;
    selectedOrganization: any;

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    startDate: Date;

    constructor(
        private journalDocumentService: JournalDocumentService,
        private translate: TranslateService,
        private locale: LocaleService,
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
    }

    ngOnInit(): void {
        this.initProcess();
        this.initSelected();
    }

    private initProcess() {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        this.currentProdJournalDocuments(1, 10);
        this.clearAllFilter();
    }

    initSelected() {
        let select = JSON.parse(localStorage.getItem("JournalDocument"));
        this.typeList = select.type;
        select = JSON.parse(localStorage.getItem("organizations"));
        this.organizations = select;
    }

    private initDefaultValues() {
        this.startDate = new Date();
        this.selectedType = "ALL";
        this.selectedOrganization = "ALL";
        this.selectedSuccess = {type: 'ALL', selected: true};
        this.filter = new JournalDocumentFilterProcessResult();
        if (this.tableHeaderSortModels.length == 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_TYPE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_SUCCESS, columnClick: 0
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

    loadType() {
        if (this.selectedType === null) {
            this.selectedType = 'ALL';
        }
        this.pagination.currentPage = 1;
        this.filter.type = this.selectedType;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadSuccess() {
        if (this.selectedSuccess === null) {
            this.selectedSuccess = {type: 'ALL', selected: true};
        }
        this.pagination.currentPage = 1;
        this.filter.success = this.selectedSuccess.type;
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

    loadReceivedDate(date: Date[]) {
        this.pagination.currentPage = 1;
        this.filter.dateRange = new DateRangeModel();
        this.filter.dateRange.dateStart = date[0];
        this.filter.dateRange.dateEnd = date[1];
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    private clickProcess(columnName: string) {
        let countClick = this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick;
        countClick++;
        if (countClick > 2) {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = 0;
        } else {
            this.tableHeaderSortModels.find(k => k.columnName === columnName).columnClick = countClick;
        }
        this.clearFilter(columnName);
    }

    private loadPage(page: number, pageSize: number) {
        this.currentProdJournalDocuments(page, pageSize);
    }

    private currentProdJournalDocuments(currentPage: number, sizeElement: number) {
        this.journalDocumentService.getListJournalDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.journalDocuments = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.selectedType = "ALL";
        this.selectedOrganization = 'ALL';
        this.textDocument = '';
        this.textMessage = '';
        this.textDurationMs = '';
        this.clearCounts();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
        this.clearCounts();
    }

    private clearCounts() {
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickCreateTime = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_CREATE_TIME).columnClick;
        this.filter.countClickDocumentProcessStepType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_TYPE).columnClick;
        this.filter.countClickSuccess = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_SUCCESS).columnClick;
        this.filter.countClickMessage = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_MESSAGE).columnClick;
        this.filter.countClickDurationMs = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DURATIOM_MS).columnClick;
    }
}