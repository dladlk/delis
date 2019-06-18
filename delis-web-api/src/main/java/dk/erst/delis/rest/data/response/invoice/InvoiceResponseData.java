package dk.erst.delis.rest.data.response.invoice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponseData {

//    private Document document;
//    private List<DocumentStatus> documentStatusList;
//    private List<JournalDocument> lastJournalList;
//    private Map<Long, List<ErrorDictionaryData>> errorListByJournalDocumentIdMap;
//    private List<DocumentBytes> documentBytes;
//    private InvoiceResponseForm irForm;
//    private MessageLevelResponseForm mlrForm;

    private String[][] invoiceResponseUseCaseList;
    private String[][] invoiceStatusCodeList;
    private String[][] statusActionList;
    private String[][] statusReasonList;

    private String[][] messageLevelResponseUseCaseList;
    private String[][] applicationResponseTypeCodeList;
    private String[][] applicationResponseLineResponseCodeList;
    private String[][] applicationResponseLineReasonCodeList;
}
