import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../../router.animations";
import { DocumentsService } from "../../services/documents.service";
import { JournalDocumentService } from "../../../journal/document/services/journal.document.service";
import { LocaleService } from "../../../../../service/locale.service";
import { HeaderModel } from "../../../../components/header/header.model";
import { DocumentModel } from "../../models/document.model";
import { ErrorService } from "../../../../../service/error.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";
import { PaginationModel } from "../../../../bs-component/components/pagination/pagination.model";
import { PaginationService } from "../../../../bs-component/components/pagination/pagination.service";
import { JournalDocumentFilterProcessResult } from "../../../journal/document/models/journal.document.filter.process.result";
import { JournalDocumentModel } from "../../../journal/document/models/journal.document.model";
import { TableHeaderSortModel } from "../../../../bs-component/components/table-header-sort/table.header.sort.model";
import { successList } from "../../../journal/document/models/journal.document.view.model";

const COLUMN_NAME_ORGANIZATION = 'journal.documents.table.columnName.Organisation';
const COLUMN_NAME_DOCUMENT = 'journal.documents.table.columnName.Document';
const COLUMN_NAME_SUCCESS = 'journal.documents.table.columnName.Success';
const COLUMN_NAME_TYPE = 'journal.documents.table.columnName.Type';
const COLUMN_NAME_MESSAGE = 'journal.documents.table.columnName.Message';
const COLUMN_NAME_DURATIOM_MS = 'journal.documents.table.columnName.DurationMs';
const COLUMN_NAME_CREATE_TIME = 'journal.documents.table.columnName.CreateTime';

@Component({
    selector: 'app-documents-one',
    templateUrl: './documents.one.component.html',
    styleUrls: ['./documents.one.component.scss'],
    animations: [routerTransition()]
})
export class DocumentsOneComponent implements OnInit {

    id: number;

    pageHeaders: HeaderModel[] = [];
    document: DocumentModel = new DocumentModel();
    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    pagination: PaginationModel;
    filter: JournalDocumentFilterProcessResult;
    journalDocuments: JournalDocumentModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private documentService: DocumentsService,
        private journalDocumentService: JournalDocumentService,
        private paginationService: PaginationService) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/documents', heading: 'documents.header', icon: 'fa fa-book'}
        );
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
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.documentService.getOneDocumentById(id).subscribe((data: DocumentModel) => {
            this.document = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
        this.id = id;
        this.initProcess(id);
    }

    clickFilter(target: string) {
        this.clickProcess(target);
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    private initProcess(id: number) {
        this.pagination = new PaginationModel();
        this.initDefaultValues();
        this.currentProdJournalDocuments(id,1, 10);
        this.clearAllFilter();
    }

    private initDefaultValues() {
        this.filter = new JournalDocumentFilterProcessResult();
        if (this.tableHeaderSortModels.length == 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_DOCUMENT, columnClick: 0
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
        this.currentProdJournalDocuments(this.id, page, pageSize);
    }

    private currentProdJournalDocuments(id: number, currentPage: number, sizeElement: number) {
        this.journalDocumentService.getAllByDocumentId(id, currentPage, sizeElement, this.filter).subscribe(
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
        this.clearCounts();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
        this.clearCounts();
    }

    private clearCounts() {
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickDocument = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DOCUMENT).columnClick;
        this.filter.countClickCreateTime = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_CREATE_TIME).columnClick;
        this.filter.countClickDocumentProcessStepType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_TYPE).columnClick;
        this.filter.countClickSuccess = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_SUCCESS).columnClick;
        this.filter.countClickMessage = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_MESSAGE).columnClick;
        this.filter.countClickDurationMs = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_DURATIOM_MS).columnClick;
    }
}
