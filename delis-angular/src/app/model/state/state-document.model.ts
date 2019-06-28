import { DocumentFilterModel } from '../filter/document-filter.model';
import { PaginationModel } from '../system/pagination.model';
import { TableHeaderSortModel } from '../system/table-header-sort.model';
import { EnumInfoModel } from '../system/enum-info.model';

const COLUMN_NAME_ORGANIZATION = 'documents.table.columnName.organisation';
const COLUMN_NAME_RECEIVER = 'documents.table.columnName.receiverIdentifier';
const COLUMN_NAME_STATUS = 'documents.table.columnName.documentStatus';
const COLUMN_NAME_LAST_ERROR = 'documents.table.columnName.lastError';
const COLUMN_NAME_DOCUMENT_TYPE = 'documents.table.columnName.documentType';
const COLUMN_NAME_INGOING_FORMAT = 'documents.table.columnName.ingoingDocumentFormat';
const COLUMN_NAME_RECEIVED = 'documents.table.columnName.createTime';
const COLUMN_NAME_SENDER_NAME = 'documents.table.columnName.senderName';

export class StateDocumentModel {

  filter: DocumentFilterModel;
  pagination: PaginationModel;
  tableHeaderSortModels: TableHeaderSortModel[] = [];
  selectedStatus: EnumInfoModel;
  selectedLastError: EnumInfoModel;
  selectedDocumentType: EnumInfoModel;
  selectedIngoingFormat: EnumInfoModel;
  selectedOrganization: string;
  textReceiver: string;
  textSenderName: string;

  constructor() {
    this.tableHeaderSortModels.push(
      {
        columnName: COLUMN_NAME_RECEIVED, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_ORGANIZATION, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_RECEIVER, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_STATUS, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_LAST_ERROR, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_DOCUMENT_TYPE, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_INGOING_FORMAT, columnClick: 0
      },
      {
        columnName: COLUMN_NAME_SENDER_NAME, columnClick: 0
      }
    );
    this.pagination = new PaginationModel();
    this.filter = new DocumentFilterModel();
  }
}
