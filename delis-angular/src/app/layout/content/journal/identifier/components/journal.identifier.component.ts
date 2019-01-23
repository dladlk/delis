import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";


import { routerTransition } from "../../../../../router.animations";
import { environment } from "../../../../../../environments/environment";
import { PaginationModel } from "../../../../bs-component/components/pagination/pagination.model";
import { TableHeaderSortModel } from "../../../../bs-component/components/table-header-sort/table.header.sort.model";
import { JournalIdentifierFilterProcessResultModel } from "../models/journal.identifier.filter.process.result.model";
import { JournalIdentifierModel } from "../models/journal.identifier.model";
import { LocaleService } from "../../../../../service/locale.service";
import { PaginationService } from "../../../../bs-component/components/pagination/pagination.service";
import { JournalIdentifierService } from "../services/journal.identifier.service";
import { JournalIdentifierTestGuiStaticService } from "../services/journal.identifier.test-gui-static.service";
import { DateRangeModel } from "../../../../../models/date.range.model";
import { ErrorService } from "../../../../../service/error.service";

const COLUMN_NAME_ORGANIZATION = 'journal.identifier.table.columnName.Organisation';
const COLUMN_NAME_IDENTIFIER = 'journal.identifier.table.columnName.Identifier';
const COLUMN_NAME_MESSAGE = 'journal.identifier.table.columnName.Message';
const COLUMN_NAME_DURATIOM_MS = 'journal.identifier.table.columnName.DurationMs';
const COLUMN_NAME_CREATE_TIME = 'journal.identifier.table.columnName.CreateTime';

@Component({
    selector: 'app-journal-identifier',
    templateUrl: './journal.identifier.component.html',
    styleUrls: ['./journal.identifier.component.scss'],
    animations: [routerTransition()]
})
export class JournalIdentifierComponent implements OnInit {

    env = environment;

    pagination: PaginationModel;
    filter: JournalIdentifierFilterProcessResultModel;
    journalIdentifiers: JournalIdentifierModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];

    textOrganisation: string;
    textIdentifier: string;
    textMessage: string;
    textDurationMs: string;

    constructor(
        private journalIdentifierService: JournalIdentifierService,
        private journalIdentifierTestGuiStaticService: JournalIdentifierTestGuiStaticService,
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
    }

    private initProcess() {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        if (this.env.production) {
            this.currentProdJournalIdentifier(1, 10);
        } else {
            this.currentDevJournalIdentifier(1, 10);
        }
        this.clearAllFilter();
    }

    private initDefaultValues() {
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

    loadTextOrganisation(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.organisation = null;
        } else {
            this.filter.organisation = text;
        }
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
        if (this.env.production) {
            this.currentProdJournalIdentifier(page, pageSize);
        } else {
            this.currentDevJournalIdentifier(page, pageSize);
        }
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

    private currentDevJournalIdentifier(currentPage: number, sizeElement: number) {
        this.journalIdentifiers = this.journalIdentifierTestGuiStaticService.filterProcess({filter: this.filter});
        this.pagination.collectionSize = this.journalIdentifiers.length;
        this.pagination.currentPage = currentPage;
        this.pagination.pageSize = sizeElement;
        let startElement = (currentPage - 1) * sizeElement;
        this.journalIdentifiers = this.journalIdentifiers.slice(startElement, startElement + sizeElement);
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.clearCounts();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
        this.clearCounts();
    }

    private clearCounts() {
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickIdentifier = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_IDENTIFIER).columnClick;
        this.filter.countClickCreateTime = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_CREATE_TIME).columnClick;
        this.filter.countClickMessage = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_MESSAGE).columnClick;
        this.filter.countClickDurationMs = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DURATIOM_MS).columnClick;
    }
}
