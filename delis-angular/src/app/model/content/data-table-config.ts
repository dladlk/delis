import { DelisDataTableColumnModel } from "./delis-data-table-column.model";

export class DataTableConfig {

    public static INIT_SEND_DOCUMENT_COLUMNS_CONFIG(): DelisDataTableColumnModel[] {
        return [
            {
                displayedColumn: 'createTime',
                typeColumn: 'date',
                headerColumn: 'documents.table.send.columnName.createTime',
                cell: (row: any) => `${row.createTime}`
            },
            {
                displayedColumn: 'organisation',
                typeColumn: 'enumString',
                headerColumn: 'documents.table.send.columnName.organisation',
                cell: (row: any) => `${row.organisation !== null ? row.organisation.name : ''}`
            },
            {
                displayedColumn: 'receiverIdRaw',
                typeColumn: 'text',
                headerColumn: 'documents.table.send.columnName.receiverIdRaw',
                cell: (row: any) => `${row.receiverIdRaw}`
            },
            {
                displayedColumn: 'senderIdRaw',
                typeColumn: 'text',
                headerColumn: 'documents.table.send.columnName.senderIdRaw',
                cell: (row: any) => `${row.senderIdRaw}`
            },
            {
                displayedColumn: 'documentStatus',
                typeColumn: 'enumInfo',
                headerColumn: 'documents.table.send.columnName.documentStatus',
                cell: (row: any) => `${row.documentStatus}`
            },
            {
                displayedColumn: 'documentType',
                typeColumn: 'enumInfo',
                headerColumn: 'documents.table.send.columnName.documentType',
                cell: (row: any) => `${row.documentType}`
            }
        ];
    }

    public static INIT_DOCUMENT_COLUMNS_CONFIG(): DelisDataTableColumnModel[] {
        return [
            {
                displayedColumn: 'createTime',
                typeColumn: 'date',
                headerColumn: 'documents.table.columnName.createTime',
                cell: (row: any) => `${row.createTime}`
            },
            {
                displayedColumn: 'organisation',
                typeColumn: 'enumString',
                headerColumn: 'documents.table.columnName.organisation',
                cell: (row: any) => `${row.organisation !== null ? row.organisation.name : ''}`
            },
            {
                displayedColumn: 'receiverIdentifier',
                typeColumn: 'text',
                headerColumn: 'documents.table.columnName.receiverIdentifier',
                cell: (row: any) => `${row.receiverIdentifier !== null ? row.receiverIdentifier.name : ''}`
            },
            {
                displayedColumn: 'documentStatus',
                typeColumn: 'enumInfo',
                headerColumn: 'documents.table.columnName.documentStatus',
                cell: (row: any) => `${row.documentStatus}`
            },
            {
                displayedColumn: 'documentType',
                typeColumn: 'enumInfo',
                headerColumn: 'documents.table.columnName.documentType',
                cell: (row: any) => `${row.documentType}`
            },
            {
                displayedColumn: 'ingoingDocumentFormat',
                typeColumn: 'enumInfo',
                headerColumn: 'documents.table.columnName.ingoingDocumentFormat',
                cell: (row: any) => `${row.ingoingDocumentFormat}`
            },
            {
                displayedColumn: 'senderName',
                typeColumn: 'text',
                headerColumn: 'documents.table.columnName.senderName',
                cell: (row: any) => `${row.senderName}`
            }
        ];
    }

    public static INIT_IDENTIFIER_COLUMNS_CONFIG(): DelisDataTableColumnModel[] {
        return [
            {
                displayedColumn: 'createTime',
                typeColumn: 'date',
                headerColumn: 'identifier.table.columnName.createTime',
                cell: (row: any) => `${row.createTime}`
            },
            {
                displayedColumn: 'organisation',
                typeColumn: 'enumString',
                headerColumn: 'identifier.table.columnName.organisation',
                cell: (row: any) => `${row.organisation !== null ? row.organisation.name : ''}`
            },
            {
                displayedColumn: 'identifierGroup',
                typeColumn: 'text',
                headerColumn: 'identifier.table.columnName.identifierGroup',
                cell: (row: any) => `${row.identifierGroup !== null ? row.identifierGroup.name : ''}`
            },
            {
                displayedColumn: 'type',
                typeColumn: 'text',
                headerColumn: 'identifier.table.columnName.type',
                cell: (row: any) => `${row.type}`
            },
            {
                displayedColumn: 'value',
                typeColumn: 'text',
                headerColumn: 'identifier.table.columnName.value',
                cell: (row: any) => `${row.value}`
            },
            {
                displayedColumn: 'uniqueValueType',
                typeColumn: 'text',
                headerColumn: 'identifier.table.columnName.uniqueValueType',
                cell: (row: any) => `${row.uniqueValueType}`
            },
            {
                displayedColumn: 'status',
                typeColumn: 'enumInfo',
                headerColumn: 'identifier.table.columnName.status',
                cell: (row: any) => `${row.status}`
            },
            {
                displayedColumn: 'name',
                typeColumn: 'text',
                headerColumn: 'identifier.table.columnName.name',
                cell: (row: any) => `${row.name}`
            }
        ];
    }
}
