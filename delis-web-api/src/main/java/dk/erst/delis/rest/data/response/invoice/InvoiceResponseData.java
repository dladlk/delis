package dk.erst.delis.rest.data.response.invoice;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.journal.JournalDocument;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;
import dk.erst.delis.web.document.ir.MessageLevelResponseForm;
import dk.erst.delis.web.error.ErrorDictionaryData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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
