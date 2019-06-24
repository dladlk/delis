import { SendDocumentsFilterProcessResult } from "./send.documents.filter.process.result";
import { TableHeaderSortModel } from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import { PaginationModel } from "../../../bs-component/components/pagination/pagination.model";
import {EnumInfoModel} from "../../../../models/enum.info.model";

const COLUMN_NAME_CREATE_TIME = 'documents.table.send.columnName.createTime';
const COLUMN_NAME_ORGANIZATION = 'documents.table.send.columnName.organisation';
const COLUMN_NAME_STATUS = 'documents.table.send.columnName.documentStatus';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.send.columnName.documentType';
const COLUMN_NAME_RECEIVER = 'documents.table.send.columnName.receiverIdRaw';
const COLUMN_NAME_SENDER = 'documents.table.send.columnName.senderIdRaw';

export class StateSendDocumentsModel {

    filter: SendDocumentsFilterProcessResult;
    pagination: PaginationModel;
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    selectedOrganization: string;
    selectedStatus: EnumInfoModel;
    selectedDocumentType: EnumInfoModel;
    textReceiver: string;
    textSender: string;

    constructor() {
        this.tableHeaderSortModels.push(
            {
                columnName: COLUMN_NAME_CREATE_TIME, columnClick: 0
            },
            {
                columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
            },
            {
                columnName: COLUMN_NAME_RECEIVER, columnClick: 0
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
        this.pagination = new PaginationModel();
        this.filter = new SendDocumentsFilterProcessResult();
    }
}
