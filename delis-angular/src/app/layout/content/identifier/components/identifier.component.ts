import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";

import { routerTransition } from "../../../../router.animations";
import { PaginationModel } from "../../../bs-component/components/pagination/pagination.model";
import { IdentifierFilterProcessResult } from "../models/identifier.filter.process.result";
import { TableHeaderSortModel } from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import { IdentifierModel } from "../models/identifier.model";
import { LocaleService } from "../../../../service/locale.service";
import { ErrorService } from "../../../../service/error.service";
import { PaginationService } from "../../../bs-component/components/pagination/pagination.service";
import { IdentifierService } from "../services/identifier.service";
import { DateRangeModel } from "../../../../models/date.range.model";
import { SHOW_DATE_FORMAT } from "../../../../app.constants";

const COLUMN_NAME_ORGANIZATION = 'identifier.table.columnName.Organisation';
const COLUMN_NAME_IDENTIFIER_GROUP = 'identifier.table.columnName.IdentifierGroup';
const COLUMN_NAME_VALUE = 'identifier.table.columnName.Value';
const COLUMN_NAME_TYPE = 'identifier.table.columnName.Type';
const COLUMN_NAME_UNIQUE_VALUE_TYPE = 'identifier.table.columnName.UniqueValueType';
const COLUMN_NAME_STATUS = 'identifier.table.columnName.Status';
const COLUMN_NAME_PUBLISHING_STATUS = 'identifier.table.columnName.PublishingStatus';
const COLUMN_NAME_NAME = 'identifier.table.columnName.Name';
const COLUMN_NAME_CREATE_TIME = 'identifier.table.columnName.CreateTime';

@Component({
    selector: 'app-identifiers',
    templateUrl: './identifier.component.html',
    styleUrls: ['./identifier.component.scss'],
    animations: [routerTransition()]
})
export class IdentifierComponent {

    pagination: PaginationModel;
    filter: IdentifierFilterProcessResult;
    identifiers: IdentifierModel[];
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    statusList: [];
    publishingStatusList: [];
    organizations: [];

    textIdentifierGroup: string;
    textType: string;
    textValue: string;
    textUniqueValueType: string;
    textName: string;

    selectedStatus: any;
    selectedPublishingStatus: any;
    selectedOrganization: any;

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;

    constructor(private translate: TranslateService,
                private locale: LocaleService,
                private errorService: ErrorService,
                private paginationService: PaginationService,
                private identifierService: IdentifierService) {
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
        this.currentProdIdentifiers(1, 10);
        this.clearAllFilter();
    }

    initSelected() {
        let select = JSON.parse(localStorage.getItem("Identifier"));
        this.statusList = select.status;
        this.publishingStatusList = select.publishingStatus;
        select = JSON.parse(localStorage.getItem("organizations"));
        this.organizations = select;
    }

    private initDefaultValues() {
        this.selectedStatus = "ALL";
        this.selectedOrganization = "ALL";
        this.selectedPublishingStatus = {type: 'ALL', selected: true};
        this.filter = new IdentifierFilterProcessResult();
        if (this.tableHeaderSortModels.length == 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_IDENTIFIER_GROUP, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_TYPE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_VALUE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_UNIQUE_VALUE_TYPE, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_STATUS, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_PUBLISHING_STATUS, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_NAME, columnClick: 0
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

    loadStatus() {
        if (this.selectedStatus === null) {
            this.selectedStatus = 'ALL';
        }
        this.pagination.currentPage = 1;
        this.filter.status = this.selectedStatus;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadPublishingStatus() {
        if (this.selectedPublishingStatus === null) {
            this.selectedPublishingStatus = 'ALL';
        }
        this.pagination.currentPage = 1;
        this.filter.publishingStatus = this.selectedPublishingStatus;
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

    loadTextIdentifierGroup(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.identifierGroup = null;
        } else {
            this.filter.identifierGroup = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextType(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.type = null;
        } else {
            this.filter.type = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextValue(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.value = null;
        } else {
            this.filter.value = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextUniqueValueType(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.uniqueValueType = null;
        } else {
            this.filter.uniqueValueType = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextName(text: string) {
        if (text.length === 0 || text == null) {
            this.filter.name = null;
        } else {
            this.filter.name = text;
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
        this.currentProdIdentifiers(page, pageSize);
    }

    private currentProdIdentifiers(currentPage: number, sizeElement: number) {
        this.identifierService.getListIdentifiers(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.identifiers = data["items"];
            }, error => {
                this.errorService.errorProcess(error);
            }
        );
    }

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.selectedStatus = "ALL";
        this.selectedPublishingStatus = "ALL";
        this.selectedOrganization = 'ALL';
        this.textIdentifierGroup = '';
        this.textType = '';
        this.textValue = '';
        this.textUniqueValueType = '';
        this.textName = '';
        this.clearCounts();
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName != columnName).forEach(cn => cn.columnClick = 0);
        this.clearCounts();
    }

    private clearCounts() {
        this.filter.countClickOrganisation = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_ORGANIZATION).columnClick;
        this.filter.countClickIdentifierGroup = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_IDENTIFIER_GROUP).columnClick;
        this.filter.countClickCreateTime = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_CREATE_TIME).columnClick;
        this.filter.countClickType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_TYPE).columnClick;
        this.filter.countClickUniqueValueType = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_UNIQUE_VALUE_TYPE).columnClick;
        this.filter.countClickValue = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_VALUE).columnClick;
        this.filter.countClickStatus = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_STATUS).columnClick;
        this.filter.countClickPublishingStatus = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_PUBLISHING_STATUS).columnClick;
        this.filter.countClickName = this.tableHeaderSortModels.find(k => k.columnName === COLUMN_NAME_NAME).columnClick;
    }
}
