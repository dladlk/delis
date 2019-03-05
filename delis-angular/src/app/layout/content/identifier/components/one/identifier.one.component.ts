import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";

import { routerTransition } from "../../../../../router.animations";
import { HeaderModel } from "../../../../components/header/header.model";
import { IdentifierModel } from "../../models/identifier.model";
import { LocaleService } from "../../../../../service/locale.service";
import { ErrorService } from "../../../../../service/error.service";
import { IdentifierService } from "../../services/identifier.service";
import { SHOW_DATE_FORMAT } from "../../../../../app.constants";
import { JournalIdentifierService } from "../../../journal/identifier/services/journal.identifier.service";
import { TableHeaderSortModel } from "../../../../bs-component/components/table-header-sort/table.header.sort.model";
import { JournalIdentifierFilterProcessResultModel } from "../../../journal/identifier/models/journal.identifier.filter.process.result.model";
import { JournalIdentifierModel } from "../../../journal/identifier/models/journal.identifier.model";

const COLUMN_NAME_ORGANIZATION = 'journal.identifier.table.columnName.organisation';
const COLUMN_NAME_IDENTIFIER = 'journal.identifier.table.columnName.identifier';
const COLUMN_NAME_MESSAGE = 'journal.identifier.table.columnName.message';
const COLUMN_NAME_DURATIOM_MS = 'journal.identifier.table.columnName.durationMs';
const COLUMN_NAME_CREATE_TIME = 'journal.identifier.table.columnName.createTime';

@Component({
    selector: 'app-identifiers-one',
    templateUrl: './identifier.one.component.html',
    styleUrls: ['./identifier.one.component.scss'],
    animations: [routerTransition()]
})
export class IdentifierOneComponent {

    id: number;

    pageHeaders: HeaderModel[] = [];
    identifier: IdentifierModel = new IdentifierModel();

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    filter: JournalIdentifierFilterProcessResultModel;
    journalIdentifiers: JournalIdentifierModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];

    constructor(
        private translate: TranslateService,
        private locale: LocaleService,
        private route: ActivatedRoute,
        private errorService: ErrorService,
        private identifierService: IdentifierService,
        private journalIdentifierService: JournalIdentifierService
    ) {
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.pageHeaders.push(
            { routerLink : '/identifiers', heading: 'identifier.header', icon: 'fa fa-id-card'}
        );
    }

    ngOnInit(): void {
        let id = Number.parseInt(this.route.snapshot.paramMap.get('id'));
        this.identifierService.getOneIdentifierById(id).subscribe((data: IdentifierModel) => {
            this.identifier = data;
        }, error => {
            this.errorService.errorProcess(error);
        });
        this.id = id;
        this.initProcess();
    }

    clickFilter(target: string) {
        this.clickProcess(target);
    }

    private initProcess() {
        this.initDefaultValues();
        this.currentProdJournalIdentifier();
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
        this.currentProdJournalIdentifier();
    }

    private currentProdJournalIdentifier() {
        this.journalIdentifierService.getAllByIdentifierId(this.id, this.filter).subscribe(
            (data: {}) => {
                this.journalIdentifiers = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
    }
}
