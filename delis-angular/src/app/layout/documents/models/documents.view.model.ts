export const dateReceiveds = [
    {dateReceived: 'LAST HOURS', selected: true},
    {dateReceived: 'LAST DAY', selected: false},
    {dateReceived: 'LAST WEEK', selected: false},
    {dateReceived: 'LAST MONTH', selected: false},
    {dateReceived: 'LAST YEAR', selected: false},
    {dateReceived: 'CUSTOM', selected: false}
];

export const statuses = [
    {status: 'ALL', selected: true},
    {status: 'LOAD_OK', selected: false},
    {status: 'VALIDATE_OK', selected: false},
    {status: 'VALIDATE_ERROR', selected: false},
    {status: 'EXPORT_OK', selected: false},
    {status: 'DELIVER_OK', selected: false}
];

export const lastErrors = [
    {lastError: 'ALL', selected: true},
    {lastError: 'BIS3_XSD', selected: false},
    {lastError: 'BIS3_SCH', selected: false},
    {lastError: 'OIOUBL_XSD', selected: false},
    {lastError: 'OIOUBL_SCH', selected: false},
    {lastError: 'CII_XSD', selected: false},
    {lastError: 'CII_SCH', selected: false}
];

export const documentTypes = [
    {documentType: 'ALL', selected: true},
    {documentType: 'UNSUPPORTED', selected: false},
    {documentType: 'INVOICE', selected: false},
    {documentType: 'CREDITNOTE', selected: false}
];

export const ingoingFormats = [
    {ingoingFormat: 'ALL', selected: true},
    {ingoingFormat: 'BIS3_INVOICE', selected: false},
    {ingoingFormat: 'BIS3_CREDITNOTE', selected: false},
    {ingoingFormat: 'OIOUBL_INVOICE', selected: false},
    {ingoingFormat: 'OIOUBL_CREDITNOTE', selected: false}
];

