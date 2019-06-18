import {Component, OnInit} from "@angular/core";
import {routerTransition} from "../../../../router.animations";
import {PaginationModel} from "../../../bs-component/components/pagination/pagination.model";
import {TableHeaderSortModel} from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import {SendDocumentsModel} from "../models/send.documents.model";
import {SendDocumentsFilterProcessResult} from "../models/send.documents.filter.process.result";
import {SHOW_DATE_FORMAT} from "../../../../app.constants";
import {TranslateService} from "@ngx-translate/core";
import {LocaleService} from "../../../../service/locale.service";
import {ErrorService} from "../../../../service/error.service";
import {PaginationService} from "../../../bs-component/components/pagination/pagination.service";
import {DaterangeService} from "../../../bs-component/components/daterange/daterange.service";
import {DaterangeShowService} from "../../../bs-component/components/daterange/daterange.show.service";
import {DateRangeModel} from "../../../../models/date.range.model";
import {SendDocumentsService} from "../service/send.documents.service";
import {EnumInfoModel} from "../../../../models/enum.info.model";

const COLUMN_NAME_CREATE_TIME = 'documents.table.send.columnName.createTime';
const COLUMN_NAME_ORGANIZATION = 'documents.table.send.columnName.organisation';
const COLUMN_NAME_STATUS = 'documents.table.send.columnName.documentStatus';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.send.columnName.documentType';
const COLUMN_NAME_RECEINER = 'documents.table.send.columnName.receiverIdRaw';
const COLUMN_NAME_SENDER = 'documents.table.send.columnName.senderIdRaw';

@Component({
    selector: 'app-send-document',
    templateUrl: './send.document.component.html',
    styleUrls: ['./send.document.component.scss'],
    animations: [routerTransition()]
})
export class SendDocumentsComponent implements OnInit {

    clearableSelect = true;

    pagination: PaginationModel;
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    filter: SendDocumentsFilterProcessResult;
    sendDocuments: SendDocumentsModel[] = [];

    organizations: string[] = [];
    statuses: EnumInfoModel[] = [];
    documentTypes: EnumInfoModel[] = [];

    SHOW_DATE_FORMAT = SHOW_DATE_FORMAT;
    show: boolean;

    selectedOrganization: any;
    selectedStatus: EnumInfoModel = new EnumInfoModel();
    selectedDocumentType: EnumInfoModel = new EnumInfoModel();

    textReceiver: string;
    textSender: string;

    constructor(
        private translate: TranslateService,
        private sendDocumentsService: SendDocumentsService,
        private locale: LocaleService,
        private errorService: ErrorService,
        private paginationService: PaginationService,
        private dtService: DaterangeService,
        private dtShowService: DaterangeShowService) {
        this.show = false;
        this.translate.use(locale.getlocale().match(/en|da/) ? locale.getlocale() : 'en');
        this.paginationService.listen().subscribe((pag: PaginationModel) => {
            if (pag.collectionSize !== 0) {
                if (pag.collectionSize <= pag.pageSize) {
                    this.loadPage(1, this.pagination.pageSize);
                } else {
                    this.loadPage(pag.currentPage, pag.pageSize);
                }
            } else {
                this.initDefaultValues();
                this.clearAllFilter();
                this.loadPage(pag.currentPage, pag.pageSize);
            }
            this.pagination = pag;
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
        this.currentProdSendDocuments(1, 10);
        this.clearAllFilter();
    }

    initSelected() {
        let select = JSON.parse(localStorage.getItem('SendDocument'));
        this.statuses = select.documentStatus;
        this.documentTypes = select.documentType;
        select = JSON.parse(localStorage.getItem('organizations'));
        this.organizations = select;
    }

    private loadPage(page: number, pageSize: number) {
        this.currentProdSendDocuments(page, pageSize);
    }

    loadDocumentType() {
        this.filter.documentType = this.selectedDocumentType.name;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadStatus() {
        this.filter.documentStatus = this.selectedStatus.name;
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextSender(text: string) {
        if (text.length === 0 || text === null) {
            this.filter.senderIdRaw = null;
        } else {
            this.filter.senderIdRaw = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    loadTextReceiver(text: string) {
        if (text.length === 0 || text === null) {
            this.filter.receiverIdRaw = null;
        } else {
            this.filter.receiverIdRaw = text;
        }
        this.pagination.currentPage = 1;
        this.loadPage(this.pagination.currentPage, this.pagination.pageSize);
    }

    private initDefaultValues() {
        this.selectedOrganization = 'ALL';
        this.selectedDocumentType.name = 'ALL';
        this.selectedDocumentType.viewName = 'ALL';
        this.selectedStatus.name = 'ALL';
        this.selectedStatus.viewName = 'ALL';
        this.filter = new SendDocumentsFilterProcessResult();
        if (this.tableHeaderSortModels.length === 0) {
            this.tableHeaderSortModels.push(
                {
                    columnName: COLUMN_NAME_CREATE_TIME, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_RECEINER, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_SENDER, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_STATUS, columnClick: 0
                },
                {
                    columnName: COLUMN_NAME_DOCUMENT_TYPE, columnClick: 0
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

    private currentProdSendDocuments(currentPage: number, sizeElement: number) {
        this.sendDocumentsService.getListSendDocuments(currentPage, sizeElement, this.filter).subscribe(
            (data: {}) => {
                this.pagination.collectionSize = data["collectionSize"];
                this.pagination.currentPage = data["currentPage"];
                this.pagination.pageSize = data["pageSize"];
                this.sendDocuments = data["items"];
                this.show = true;
            }, error => {
                this.errorService.errorProcess(error);
                this.show = false;
            }
        );
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

    private clearAllFilter() {
        this.tableHeaderSortModels.forEach(cn => cn.columnClick = 0);
        this.selectedOrganization = 'ALL';
        this.selectedDocumentType = new EnumInfoModel();
        this.selectedStatus = new EnumInfoModel();
        this.filter.dateRange = null;
    }

    private clearFilter(columnName: string) {
        this.tableHeaderSortModels.filter(cn => cn.columnName !== columnName).forEach(cn => cn.columnClick = 0);
    }
}
