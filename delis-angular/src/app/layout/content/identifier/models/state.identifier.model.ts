import { PaginationModel } from "../../../bs-component/components/pagination/pagination.model";
import { TableHeaderSortModel } from "../../../bs-component/components/table-header-sort/table.header.sort.model";
import { IdentifierFilterProcessResult } from "./identifier.filter.process.result";
import { EnumInfoModel } from "../../../../models/enum.info.model";

const COLUMN_NAME_ORGANIZATION = 'identifier.table.columnName.organisation';
const COLUMN_NAME_IDENTIFIER_GROUP = 'identifier.table.columnName.identifierGroup';
const COLUMN_NAME_VALUE = 'identifier.table.columnName.value';
const COLUMN_NAME_TYPE = 'identifier.table.columnName.type';
const COLUMN_NAME_UNIQUE_VALUE_TYPE = 'identifier.table.columnName.uniqueValueType';
const COLUMN_NAME_STATUS = 'identifier.table.columnName.status';
const COLUMN_NAME_PUBLISHING_STATUS = 'identifier.table.columnName.publishingStatus';
const COLUMN_NAME_NAME = 'identifier.table.columnName.name';
const COLUMN_NAME_CREATE_TIME = 'identifier.table.columnName.createTime';

export class StateIdentifierModel {

    filter: IdentifierFilterProcessResult;
    pagination: PaginationModel;
    tableHeaderSortModels: TableHeaderSortModel[] = [];
    textIdentifierGroup: string;
    textType: string;
    textValue: string;
    textUniqueValueType: string;
    textName: string;
    selectedStatus: EnumInfoModel;
    selectedPublishingStatus: EnumInfoModel;
    selectedOrganization: string;

    constructor() {
        this.tableHeaderSortModels.push(
            {
                columnName: COLUMN_NAME_CREATE_TIME, columnClick: 0
            },
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
            }
        );
        this.pagination = new PaginationModel();
        this.filter = new IdentifierFilterProcessResult();
    }
}
